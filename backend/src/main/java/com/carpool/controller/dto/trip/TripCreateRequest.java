package com.carpool.controller.dto.trip;

import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;

public record TripCreateRequest(
        @NotNull(message = "ID офиса не может быть пустым")
        Long officeId,

        @NotNull(message = "Время отправления обязательно")
        @Future(message = "Время отправления должно быть в будущем")
        OffsetDateTime departureTime,

        @NotNull(message = "Примерное время в пути обязательно")
        @Min(value = 1, message = "Время в пути должно быть больше 0")
        Integer estimatedDuration,

        @NotNull(message = "Количество мест обязательно")
        @Min(value = 1, message = "Должно быть минимум 1 свободное место")
        @Max(value = 6, message = "Количество мест не может превышать 6")
        Integer totalSeats,

        @NotBlank(message = "Модель машины не может быть пустой")
        @Size(max = 100, message = "Название модели слишком длинное")
        String carModel,

        @NotBlank(message = "Цвет машины обязателен")
        @Size(max = 50, message = "Название цвета слишком длинное")
        String carColor,

        @NotBlank(message = "Номер машины обязателен")
        @Size(max = 20, message = "Номер машины слишком длинный")
        String carPlate,

        @NotNull(message = "Маршрут не может быть null")
        @Size(min = 2, message = "Маршрут должен состоять как минимум из начальной и конечной точек")
        double[][] routePath
) {}