package com.carpool.controller.dto.trip;

import com.carpool.domain.model.trip.TripStatus;
import java.time.OffsetDateTime;

public record TripResponse(
        Long id,
        Long officeId,
        OffsetDateTime departureTime,
        Integer estimatedDuration,
        Integer totalSeats,
        Integer availableSeats,
        String carModel,
        String carColor,
        String carPlate,
        TripStatus status,
        double[][] routePath
) {}