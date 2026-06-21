package com.carpool.controller.dto.trip;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;

@Schema(description = "Запрос на создание поездки водителем")
public record TripCreateRequest(
        @NotNull(message = "ID офиса не может быть пустым")
        @Schema(description = "ID офиса назначения", example = "1")
        Long officeId,

        @NotNull(message = "Время отправления обязательно")
        @Future(message = "Время отправления должно быть в будущем")
        @Schema(description = "Время отправления", example = "2026-06-20T19:00:00Z")
        OffsetDateTime departureTime,

        @NotNull(message = "Примерное время в пути обязательно")
        @Min(value = 1, message = "Время в пути должно быть больше 0")
        @Schema(description = "Приблизительное время в пути")
        Integer estimatedDuration,

        @NotNull(message = "Количество мест обязательно")
        @Min(value = 1, message = "Должно быть минимум 1 свободное место")
        @Max(value = 6, message = "Количество мест не может превышать 6")
        @Schema(description = "Количество свободных мест в машине", example = "3")
        Integer totalSeats,

        @NotBlank(message = "Модель машины не может быть пустой")
        @Size(max = 100, message = "Название модели слишком длинное")
        @Schema(description = "Модель машины", example = "Mercedes GLE")
        String carModel,

        @NotBlank(message = "Цвет машины обязателен")
        @Size(max = 50, message = "Название цвета слишком длинное")
        @Schema(description = "Цвет машины", example = "Черный")
        String carColor,

        @NotBlank(message = "Номер машины обязателен")
        @Size(max = 20, message = "Номер машины слишком длинный")
        @Schema(description = "Номер машины", example = "А777АА77")
        String carPlate,

        @NotNull(message = "Маршрут не может быть null")
        @Size(min = 2, message = "Маршрут должен состоять как минимум из начальной и конечной точек")
        @Schema(description = "Маршрут следования")
        double[][] routePath
) {}