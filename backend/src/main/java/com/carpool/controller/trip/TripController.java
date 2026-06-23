package com.carpool.controller.trip;

import com.carpool.controller.dto.trip.TripCreateRequest;
import com.carpool.controller.dto.trip.TripDetailedResponse;
import com.carpool.controller.dto.trip.TripResponse;
import com.carpool.controller.dto.trip.TripUpdateRequest;
import com.carpool.domain.model.trip.Trip;
import com.carpool.domain.model.user.User;
import com.carpool.domain.service.TripService;
import com.carpool.domain.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trips")
@Tag(name = "Trips", description = "Управление поездками (для водителей и поиска для пассажиров)")
public class TripController {

    private final TripService tripService;
    private final TripWebMapper tripWebMapper;
    private final UserService userService;

    public TripController(TripService tripService, TripWebMapper tripWebMapper, UserService userService) {
        this.tripService = tripService;
        this.tripWebMapper = tripWebMapper;
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Создать поездку", description = "Создает новую поездку. Водитель не должен иметь активной заявки пассажира.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Поездка успешно создана"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Ошибка валидации данных\"}"))),
            @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Не авторизован\"}"))),
            @ApiResponse(responseCode = "409", description = "Уже есть активная заявка или поездка",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Уже есть активная поездка\"}")))
    })
    public ResponseEntity<TripResponse> createTrip(
            @Valid @RequestBody TripCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();

        Trip tripToCreate = tripWebMapper.toDomain(request);
        tripToCreate.setDriverId(userId);

        Trip createdTrip = tripService.createTrip(tripToCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(tripWebMapper.toDto(createdTrip));
    }

    @GetMapping("/my-active")
    @Operation(summary = "Получить мою активную поездку", description = "Возвращает текущую поездку в статусе CREATED или IN_PROGRESS")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Поездка найдена"),
            @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Не авторизован\"}"))),
            @ApiResponse(responseCode = "404", description = "Активных поездок нет",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Поездка не найдена\"}")))
    })
    public ResponseEntity<TripResponse> getActiveTrip(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();

        return tripService.getActiveTrip(userId)
                .map(tripWebMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить поездку", description = "Редактирование маршрута/мест. Доступно только водителю (создателю) до старта.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Поездка обновлена"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Ошибка валидации данных\"}"))),
            @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Не авторизован\"}"))),
            @ApiResponse(responseCode = "400", description = "Нет прав на изменение чужой поездки",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Попытка изменить чужую поездку\"}"))),
            @ApiResponse(responseCode = "404", description = "Активных поездок нет",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Поездка не найдена\"}")))
    })
    public ResponseEntity<TripResponse> updateTrip(
            @PathVariable Long id,
            @Valid @RequestBody TripUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();
        Trip updatedData = tripWebMapper.toDomain(request);

        Trip savedTrip = tripService.updateTrip(id, userId, updatedData);

        return ResponseEntity.ok(tripWebMapper.toDto(savedTrip));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Отменить поездку", description = "Переводит поездку в статус CANCELED и рассылает уведомления пассажирам")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Поездка отменена"),
            @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Не авторизован\"}"))),
            @ApiResponse(responseCode = "403", description = "Попытка отменить чужую поездку",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Попытка отменить чужую поездку\"}"))),
            @ApiResponse(responseCode = "404", description = "Поездка не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Поездка не найдена\"}")))
    })
    public ResponseEntity<TripResponse> cancelTrip(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();
        Trip canceledTrip = tripService.cancelTrip(id, userId);

        return ResponseEntity.ok(tripWebMapper.toDto(canceledTrip));
    }

    @GetMapping("/matching")
    @Operation(summary = "Умный поиск поездок (Matching)", description = "Ищет поездки (в радиусе 1.5км) на основе активной заявки пассажира")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список подходящих поездок"),
            @ApiResponse(responseCode = "400", description = "Нет активной заявки пассажира с точкой посадки",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Нет активной заявки\"}"))),
            @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Не авторизован\"}")))
    })
    public ResponseEntity<List<TripDetailedResponse>> getMatchingTrips(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long passengerId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();
        List<Trip> matchingTrips = tripService.findMatchingTripsForPassenger(passengerId);

        List<TripDetailedResponse> response = matchingTrips.stream()
                .map(trip -> {
                    User driver = null;
                    try {
                        driver = userService.getUserProfile(trip.getDriverId());
                    } catch (IllegalArgumentException e) {
                        driver = new User();
                        driver.setFirstName("Удаленный");
                        driver.setLastName("Пользователь");
                    }
                    return tripWebMapper.toDetailedDto(trip, driver);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Ручной поиск поездок", description = "Возвращает все активные поездки в выбранный офис")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список поездок"),
            @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"Не авторизован\"}")))
    })
    public ResponseEntity<List<TripResponse>> getAvailableTrips(
            @RequestParam Long officeId) {

        List<TripResponse> response = tripService.getAvailableTripsForOffice(officeId).stream()
                .map(tripWebMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}