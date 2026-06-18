package com.carpool.controller.dto.trip;

import java.time.OffsetDateTime;

public record TripCreateRequest(
        Long officeId,
        OffsetDateTime departureTime,
        Integer estimatedDuration,
        Integer totalSeats,
        String carModel,
        String carColor,
        String carPlate,
        double[][] routePath
) {

}
