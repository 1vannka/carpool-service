package com.carpool.domain.repository;

import com.carpool.domain.model.trip.Trip;
import org.locationtech.jts.geom.Point;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface TripRepositoryPort {
    Trip save(Trip trip);
    Optional<Trip> findActiveTripByDriverId(Long driverId);
    Optional<Trip> findById(Long id);
    List<Trip> findMatchingTrips(Long officeId, OffsetDateTime timeMin, OffsetDateTime timeMax, Point pickupLocation, double radiusMeters);
}
