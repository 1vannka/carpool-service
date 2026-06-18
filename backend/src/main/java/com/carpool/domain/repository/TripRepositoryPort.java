package com.carpool.domain.repository;

import com.carpool.domain.model.trip.Trip;

import java.util.Optional;

public interface TripRepositoryPort {
    Trip save(Trip trip);
    Optional<Trip> findActiveTripByDriverId(Long driverId);
    Optional<Trip> findById(Long id);
}
