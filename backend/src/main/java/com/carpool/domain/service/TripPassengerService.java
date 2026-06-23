package com.carpool.domain.service;

import com.carpool.domain.model.trip.TripPassenger;

import java.util.List;
import java.util.Optional;

public interface TripPassengerService {
    TripPassenger requestToJoin(Long tripId, Long passengerId);
    TripPassenger approvePassenger(Long tripId, Long passengerId, Long driverId);
    void rejectPassenger(Long tripId, Long passengerId, Long driverId);
    void cancelPassengerRequest(Long tripId, Long passengerId);
    List<TripPassenger> getPassengersByTripId(Long tripId);
    Optional<TripPassenger> getPassengerStatus(Long tripId, Long passengerId);
    void pingPassenger(Long tripId, Long passengerId, Long driverId);
}