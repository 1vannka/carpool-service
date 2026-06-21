package com.carpool.controller.dto.ride;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;

@Schema(description = "Запрос на создание заявки на поездку")
public record RideRequestCreateRequest(
        @NotNull(message = "ID офиса не может быть пустым")
        @Schema(description = "ID офиса назначения", example = "1")
        Long officeId,

        @NotNull(message = "Локация посадки обязательна")
        @Size(min = 2, max = 2, message = "Локация должна содержать координаты")
        @Schema(description = " Координаты локации посадки")
        double[] pickupLocation,

        @NotNull(message = "Желаемое время выезда обязательно")
        @Future(message = "Желаемое время выезда должно быть в будущем")
        @Schema(description = "Желаемое время выезда", example = "2026-06-20T19:00:00Z")
        OffsetDateTime targetTime,

        @NotNull(message = "Допустимое отклонение времени обязательно")
        @Min(value = 0, message = "Отклонение времени не может быть отрицательным")
        @Max(value = 60, message = "Отклонение времени не может превышать 60 минут")
        @Schema(description = "Допустимое отклонение времени выезда в минутах", example = "15")
        Integer toleranceTime
) {}