package com.carpool.controller.dto.ride;

import com.carpool.domain.model.ride.RideRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Ответ с данными запрашиваемой заявки на поездку")
public record RideRequestResponse(
        @Schema(description = "ID заявки")
        Long id,

        @Schema(description = "ID пассажира")
        Long passengerId,

        @Schema(description = "ID офиса назначения")
        Long officeId,

        @Schema(description = " Координаты локации посадки")
        double[] pickupLocation,

        @Schema(description = "Желаемое время выезда", example = "2026-06-20T19:00:00Z")
        OffsetDateTime targetTime,

        @Schema(description = "Допустимое отклонение времени выезда в минутах", example = "15")
        Integer toleranceTime,

        @Schema(description = "Статус заявки", example = "PENDING")
        RideRequestStatus status
) {}