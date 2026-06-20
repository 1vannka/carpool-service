package com.carpool.controller.dto.ride;

import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;

public record RideRequestCreateRequest(
        @NotNull(message = "ID офиса не может быть пустым")
        Long officeId,

        @NotNull(message = "Локация посадки обязательна")
        @Size(min = 2, max = 2, message = "Локация должна содержать координаты")
        double[] pickupLocation,

        @NotNull(message = "Желаемое время выезда обязательно")
        @Future(message = "Желаемое время выезда должно быть в будущем")
        OffsetDateTime targetTime,

        @NotNull(message = "Допустимое отклонение времени обязательно")
        @Min(value = 0, message = "Отклонение времени не может быть отрицательным")
        @Max(value = 60, message = "Отклонение времени не может превышать 60 минут")
        Integer toleranceTime
) {}