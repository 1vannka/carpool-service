package com.carpool.controller.trip;

import com.carpool.controller.dto.trip.TripPassengerDetailedResponse;
import com.carpool.controller.dto.trip.TripPassengerResponse;
import com.carpool.domain.model.trip.TripPassenger;
import com.carpool.domain.service.TripPassengerService;
import com.carpool.domain.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trips/{tripId}")
@Tag(name = "Trip Passengers", description = "Бронирование мест и управление пассажирами")
public class TripPassengerController {

    private final TripPassengerService tripPassengerService;
    private final TripPassengerWebMapper webMapper;
    private final UserService userService;

    public TripPassengerController(TripPassengerService tripPassengerService, TripPassengerWebMapper webMapper, UserService userService) {
        this.tripPassengerService = tripPassengerService;
        this.webMapper = webMapper;
        this.userService = userService;
    }

    @GetMapping("/passengers")
    @Operation(summary = "Получить список пассажиров поездки", description = "Возвращает всех пассажиров (активных и ожидающих) для указанной поездки. Доступно водителю.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список пассажиров успешно получен"),
            @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Не авторизован\"}"))),
            @ApiResponse(responseCode = "404", description = "Поездка не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Поездка не найдена\"}")))
    })
    public ResponseEntity<List<TripPassengerDetailedResponse>> getTripPassengers(@PathVariable Long tripId) {
        List<TripPassenger> domainPassengers = tripPassengerService.getPassengersByTripId(tripId);

        List<TripPassengerDetailedResponse> response = domainPassengers.stream()
                .map(passenger -> {
                    try {
                        return webMapper.toDetailedDto(passenger, userService.getUserProfile(passenger.getPassengerId()));
                    } catch (Exception e) {
                        return new TripPassengerDetailedResponse(
                                passenger.getTripId(),
                                "Удаленный",
                                "Пользователь",
                                passenger.getPassengerId(),
                                passenger.getStatus()
                        );
                    }
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-status")
    @Operation(summary = "Получить статус моей заявки", description = "Проверяет статус бронирования текущего авторизованного пользователя в этой поездке")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус успешно получен"),
            @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Не авторизован\"}"))),
            @ApiResponse(responseCode = "404", description = "Заявка на эту поездку отсутствует",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Заявка не найдена\"}")))
    })
    public ResponseEntity<TripPassengerResponse> getMyStatus(
            @PathVariable Long tripId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long passengerId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();

        return tripPassengerService.getPassengerStatus(tripId, passengerId)
                .map(webMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/join")
    @Operation(summary = "Подать заявку на присоединение", description = "Создает запрос к водителю (статус WAITING_APPROVAL)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Заявка отправлена"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Ошибка валидации данных\"}"))),
            @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Не авторизован\"}"))),
            @ApiResponse(responseCode = "404", description = "Поездка не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Поездка не найдена\"}"))),
            @ApiResponse(responseCode = "404", description = "Заявка на эту поездку уже подавалась",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Заявка на эту поездку уже подавалась\"}")))
    })
    public ResponseEntity<TripPassengerResponse> joinTrip(
            @PathVariable Long tripId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long passengerId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();
        TripPassenger request = tripPassengerService.requestToJoin(tripId, passengerId);

        return ResponseEntity.status(HttpStatus.CREATED).body(webMapper.toDto(request));
    }

    @PostMapping("/passengers/{passengerId}/approve")
    @Operation(summary = "Одобрить пассажира", description = "Уменьшает количество свободных мест и отправляет пуш пассажиру. Доступно только водителю.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пассажир одобрен"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Ошибка валидации данных\"}"))),
            @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Не авторизован\"}"))),
            @ApiResponse(responseCode = "403", description = "Вы не водитель этой поездки",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Вы не водитель этой поездки\"}"))),
            @ApiResponse(responseCode = "404", description = "Заявка или поездка не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Заявка или поездка не найдена\"}")))
    })
    public ResponseEntity<TripPassengerResponse> approvePassenger(
            @PathVariable Long tripId,
            @PathVariable Long passengerId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long driverId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();
        TripPassenger approvedRequest = tripPassengerService.approvePassenger(tripId, passengerId, driverId);

        return ResponseEntity.ok(webMapper.toDto(approvedRequest));
    }

    @DeleteMapping("/passengers/{passengerId}/reject")
    @Operation(summary = "Отклонить пассажира", description = "Оставляет заявке статус REJECTED. Доступно только водителю.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пассажир отклонен"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Ошибка валидации данных\"}"))),
            @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Не авторизован\"}"))),
            @ApiResponse(responseCode = "403", description = "Вы не водитель этой поездки",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Вы не водитель этой поездки\"}"))),
            @ApiResponse(responseCode = "404", description = "Заявка не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Заявка не найдена\"}")))
    })
    public ResponseEntity<Void> rejectPassenger(
            @PathVariable Long tripId,
            @PathVariable Long passengerId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long driverId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();
        tripPassengerService.rejectPassenger(tripId, passengerId, driverId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/passengers/my-request")
    @Operation(summary = "Отменить свою заявку", description = "Пассажир отменяет свою заявку")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Заявка успешно отменена"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Ошибка валидации данных\"}"))),
            @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Не авторизован\"}"))),
            @ApiResponse(responseCode = "404", description = "Заявка не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Заявка не найдена\"}")))
    })
    public ResponseEntity<Void> cancelMyRequest(
            @PathVariable Long tripId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long passengerId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();
        tripPassengerService.cancelPassengerRequest(tripId, passengerId);

        return ResponseEntity.noContent().build();
    }
}