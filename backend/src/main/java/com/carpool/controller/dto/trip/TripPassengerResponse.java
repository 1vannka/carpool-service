package com.carpool.controller.dto.trip;

import com.carpool.domain.model.trip.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с данными запрашиваемой связи пассажира и поездки")
public record TripPassengerResponse(
        @Schema(description = "ID поездки", example = "1")
        Long tripId,
        @Schema(description = "ID пассажира", example = "1")
        Long passengerId,
        @Schema(description = "Статус", example = "WAITING_APPROVAL")
        BookingStatus status
) {}