package com.carpool.controller.dto.trip;

import com.carpool.domain.model.trip.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Развернутый ответ с данными запрашиваемой заявки")
public record TripPassengerDetailedResponse(
        @Schema(description = "ID поездки", example = "1")
        Long tripId,

        @Schema(description = "Имя пассажира", example = "Иван")
        String firstName,

        @Schema(description = "Фамилия пассажира", example = "Иванов")
        String lastName,

        @Schema(description = "Ник/id пользователя в telegram", example = "id12345")
        String telegramAlias,

        @Schema(description = "Ник/id пользователя в Вконтакте", example = "id12345")
        String vkAlias,

        @Schema(description = "ID пассажира", example = "1")
        Long passengerId,

        @Schema(description = "Статус", example = "WAITING_APPROVAL")
        BookingStatus status,

        @Schema(description = "Точка посадки", example = "[40.0, 50.0]")
        double[] pickupLocation
) {
}
