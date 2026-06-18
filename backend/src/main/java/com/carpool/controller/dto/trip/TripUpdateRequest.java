package com.carpool.controller.dto.trip;

import java.time.OffsetDateTime;

public record TripUpdateRequest(
        OffsetDateTime departureTime,
        Integer estimatedDuration,
        Integer totalSeats,
        String carModel,
        String carColor,
        String carPlate,
        double[][] routePath
) {}
