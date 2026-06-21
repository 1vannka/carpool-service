package com.carpool.controller.ride;

import com.carpool.controller.dto.ride.RideRequestCreateRequest;
import com.carpool.controller.dto.ride.RideRequestResponse;
import com.carpool.domain.model.ride.RideRequest;
import com.carpool.domain.service.RideRequestService;
import com.carpool.domain.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ride-requests")
@Tag(name = "Ride Requests", description = "Ядро заявок пассажиров на поиск машины")
public class RideRequestController {

    private final RideRequestService rideRequestService;
    private final RideRequestWebMapper webMapper;
    private final UserService userService;

    public RideRequestController(RideRequestService rideRequestService, RideRequestWebMapper webMapper, UserService userService) {
        this.rideRequestService = rideRequestService;
        this.webMapper = webMapper;
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Создать заявку", description = "Точка посадки и желаемое время. Отменяет статус водителя.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Заявка создана"),
            @ApiResponse(responseCode = "400", description = "Время в прошлом или плохие координаты"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "409", description = "Уже есть активная заявка или поездка")
    })
    public ResponseEntity<RideRequestResponse> createRideRequest(
            @Valid @RequestBody RideRequestCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long passengerId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();

        RideRequest domainRequest = webMapper.toDomain(request);
        domainRequest.setPassengerId(passengerId);

        RideRequest createdRequest = rideRequestService.createRideRequest(domainRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(webMapper.toDto(createdRequest));
    }

    @GetMapping("/my-active")
    @Operation(summary = "Получить активную заявку", description = "Возвращает текущую заявку пассажира (статус PENDING)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заявка найдена"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Активных заявок нет")
    })
    public ResponseEntity<RideRequestResponse> getMyActiveRequest(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long passengerId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();

        return rideRequestService.getActiveRequest(passengerId)
                .map(webMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Отменить заявку", description = "Переводит заявку в статус CANCELED. Доступно только владельцу.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Заявка отменена"),
            @ApiResponse(responseCode = "400", description = "Заявка уже отменена или протухла"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Попытка удалить чужую заявку"),
            @ApiResponse(responseCode = "404", description = "Заявка не найдена")
    })
    public ResponseEntity<Void> cancelRideRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long passengerId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();
        rideRequestService.cancelRideRequest(id, passengerId);

        return ResponseEntity.noContent().build();
    }
}