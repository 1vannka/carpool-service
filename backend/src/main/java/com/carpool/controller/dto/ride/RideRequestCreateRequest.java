package com.carpool.controller.dto.ride;

import java.time.OffsetDateTime;

public record RideRequestCreateRequest(
        Long officeId,
        double[] pickupLocation,
        OffsetDateTime targetTime,
        Integer toleranceTime
) {}