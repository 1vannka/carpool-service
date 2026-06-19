package com.carpool.domain.service;

import com.carpool.domain.model.ride.RideRequest;
import java.util.Optional;

public interface RideRequestService {
    RideRequest createRideRequest(RideRequest request);
    RideRequest cancelRideRequest(Long id);
    Optional<RideRequest> getActiveRequest(Long passengerId);
}