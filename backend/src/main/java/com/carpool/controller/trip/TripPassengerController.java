package com.carpool.controller.trip;

import com.carpool.controller.dto.trip.TripPassengerResponse;
import com.carpool.domain.model.trip.TripPassenger;
import com.carpool.domain.service.TripPassengerService;
import com.carpool.domain.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/join")
    @Operation(summary = "Подать заявку на присоединение", description = "Создает запрос к водителю (статус WAITING_APPROVAL)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Заявка отправлена"),
            @ApiResponse(responseCode = "400", description = "Нет мест, или попытка стать пассажиром в своей поездке"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Поездка не найдена"),
            @ApiResponse(responseCode = "409", description = "Заявка на эту поездку уже подавалась")
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
            @ApiResponse(responseCode = "400", description = "Нет свободных мест или заявка уже обработана"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Вы не водитель этой поездки"),
            @ApiResponse(responseCode = "404", description = "Заявка или поездка не найдена")
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
    @Operation(summary = "Отклонить пассажира", description = "Оставляет заявке статус REJECTED (защита от спама). Доступно только водителю.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пассажир отклонен"),
            @ApiResponse(responseCode = "400", description = "Заявка уже отклонена"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Вы не водитель этой поездки"),
            @ApiResponse(responseCode = "404", description = "Заявка не найдена")
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
    @Operation(summary = "Отменить свою заявку", description = "Пассажир отменяет свою заявку (освобождает место, если было подтверждено)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Заявка успешно отменена"),
            @ApiResponse(responseCode = "400", description = "Нельзя отменить уже отклоненную водителем заявку (черная метка)"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Заявка не найдена")
    })
    public ResponseEntity<Void> cancelMyRequest(
            @PathVariable Long tripId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long passengerId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();
        tripPassengerService.cancelPassengerRequest(tripId, passengerId);

        return ResponseEntity.noContent().build();
    }
}