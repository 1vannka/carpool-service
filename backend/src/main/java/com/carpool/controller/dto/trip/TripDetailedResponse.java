package com.carpool.controller.dto.trip;

import com.carpool.domain.model.trip.TripStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Развернутый ответ с данными запрашиваемой поездки")
public record TripDetailedResponse(
        @Schema(description = "ID поездки", example = "1")
        Long id,

        @Schema(description = "ID офиса назначения", example = "1")
        Long officeId,

        @Schema(description = "Имя водителя", example = "Иван")
        String driverFirstName,

        @Schema(description = "Фамилия водителя", example = "Иванов")
        String driverLastName,

        @Schema(description = "Ник/id пользователя в telegram", example = "id12345")
        String telegramAlias,

        @Schema(description = "Ник/id пользователя в Вконтакте", example = "id12345")
        String vkAlias,

        @Schema(description = "Время отправления", example = "2026-06-20T19:00:00Z")
        OffsetDateTime departureTime,

        @Schema(description = "Приблизительное время в пути")
        Integer estimatedDuration,

        @Schema(description = "Изначальное количество свободных мест в машине", example = "3")
        Integer totalSeats,

        @Schema(description = "Текущее количество свободных мест в машине", example = "3")
        Integer availableSeats,

        @Schema(description = "Модель машины", example = "Mercedes GLE")
        String carModel,

        @Schema(description = "Цвет машины", example = "Черный")
        String carColor,

        @Schema(description = "Номер машины", example = "А777АА77")
        String carPlate,

        @Schema(description = "Статус поездки", example = "CREATED")
        TripStatus status,

        @Schema(description = "Маршрут следования")
        double[][] routePath,

        @Schema(description = "Начальная точка маршрута водителя")
        double[] startLocation
) {}