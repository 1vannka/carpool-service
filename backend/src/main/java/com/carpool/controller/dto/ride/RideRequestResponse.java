package com.carpool.controller.dto.ride;

import com.carpool.domain.model.ride.RideRequestStatus;
import java.time.OffsetDateTime;

public record RideRequestResponse(
        Long id,
        Long passengerId,
        Long officeId,
        double[] pickupLocation,
        OffsetDateTime targetTime,
        Integer toleranceTime,
        RideRequestStatus status
) {}