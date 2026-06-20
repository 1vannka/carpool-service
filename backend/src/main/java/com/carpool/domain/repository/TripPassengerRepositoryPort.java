package com.carpool.domain.repository;

import com.carpool.domain.model.trip.TripPassenger;
import java.util.List;
import java.util.Optional;

public interface TripPassengerRepositoryPort {
    TripPassenger save(TripPassenger tripPassenger);
    Optional<TripPassenger> findByIds(Long tripId, Long passengerId);
    void delete(Long tripId, Long passengerId);
    List<TripPassenger> findAllByTripId(Long tripId);
}