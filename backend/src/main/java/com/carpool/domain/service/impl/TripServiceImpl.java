package com.carpool.domain.service.impl;

import com.carpool.domain.exception.ActiveTripAlreadyExistsException;
import com.carpool.domain.model.trip.Trip;
import com.carpool.domain.model.trip.TripStatus;
import com.carpool.domain.repository.TripRepositoryPort;
import com.carpool.domain.service.TripService;

import java.util.Optional;

public class TripServiceImpl implements TripService {

    private final TripRepositoryPort tripRepositoryPort;

    public TripServiceImpl(TripRepositoryPort tripRepositoryPort) {
        this.tripRepositoryPort = tripRepositoryPort;
    }

    @Override
    public Trip createTrip(Trip trip) {
        Optional<Trip> activeTrip = tripRepositoryPort.findActiveTripByDriverId(trip.getDriverId());

        if (activeTrip.isPresent()) {
            throw new ActiveTripAlreadyExistsException("У вас уже есть активная поездка. Отредактируйте или отмените её.");
        }

        trip.setStatus(TripStatus.CREATED);
        trip.setAvailableSeats(trip.getTotalSeats());

        return tripRepositoryPort.save(trip);
    }

    @Override
    public Trip updateTrip(Long tripId, Trip updatedData) {
        Trip existingTrip = tripRepositoryPort.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Поездка не найдена"));

        if (existingTrip.getStatus() != TripStatus.CREATED) {
            throw new IllegalStateException("Нельзя редактировать поездку, которая уже началась или завершена");
        }

        existingTrip.setDepartureTime(updatedData.getDepartureTime());
        existingTrip.setEstimatedDuration(updatedData.getEstimatedDuration());
        existingTrip.setTotalSeats(updatedData.getTotalSeats());
        existingTrip.setAvailableSeats(updatedData.getTotalSeats());
        existingTrip.setCarModel(updatedData.getCarModel());
        existingTrip.setCarColor(updatedData.getCarColor());
        existingTrip.setCarPlate(updatedData.getCarPlate());
        existingTrip.setRoutePath(updatedData.getRoutePath());

        return tripRepositoryPort.save(existingTrip);
    }

    @Override
    public Trip cancelTrip(Long tripId) {
        Trip existingTrip = tripRepositoryPort.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Поездка не найдена"));

        existingTrip.setStatus(TripStatus.CANCELED);
        return tripRepositoryPort.save(existingTrip);
    }

    @Override
    public Optional<Trip> getActiveTrip(Long driverId) {
        return tripRepositoryPort.findActiveTripByDriverId(driverId);
    }
}