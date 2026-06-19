package com.carpool.domain.service;

import com.carpool.domain.model.trip.TripPassenger;

public interface TripPassengerService {
    TripPassenger requestToJoin(Long tripId, Long passengerId);
    TripPassenger approvePassenger(Long tripId, Long passengerId, Long driverId);
    void rejectPassenger(Long tripId, Long passengerId, Long driverId);
}