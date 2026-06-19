package com.carpool.controller.dto.trip;

import com.carpool.domain.model.trip.BookingStatus;

public record TripPassengerResponse(
        Long tripId,
        Long passengerId,
        BookingStatus status
) {}