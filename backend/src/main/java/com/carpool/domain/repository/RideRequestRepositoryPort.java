package com.carpool.domain.repository;

import com.carpool.domain.model.ride.RideRequest;
import java.util.Optional;

public interface RideRequestRepositoryPort {
    RideRequest save(RideRequest rideRequest);
    Optional<RideRequest> findById(Long id);
    Optional<RideRequest> findPendingByPassengerId(Long passengerId);
}