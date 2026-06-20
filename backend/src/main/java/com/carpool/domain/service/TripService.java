package com.carpool.domain.service;

import com.carpool.domain.model.trip.Trip;

import java.util.List;
import java.util.Optional;

public interface TripService {
    Trip createTrip(Trip trip);
    Trip updateTrip(Long tripId, Long driverId, Trip updatedData);
    Trip cancelTrip(Long tripId, Long driverId);
    Optional<Trip> getActiveTrip(Long driverId);
    List<Trip> findMatchingTripsForPassenger(Long passengerId);
}
