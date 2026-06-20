package com.carpool.domain.service.impl;

import com.carpool.domain.exception.ActiveTripAlreadyExistsException;
import com.carpool.domain.model.ride.RideRequest;
import com.carpool.domain.model.trip.Trip;
import com.carpool.domain.model.trip.TripPassenger;
import com.carpool.domain.model.trip.TripStatus;
import com.carpool.domain.repository.RideRequestRepositoryPort;
import com.carpool.domain.repository.TripPassengerRepositoryPort;
import com.carpool.domain.repository.TripRepositoryPort;
import com.carpool.domain.service.NotificationService;
import com.carpool.domain.service.TripService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public class TripServiceImpl implements TripService {

    private final TripRepositoryPort tripRepositoryPort;
    private final RideRequestRepositoryPort rideRequestRepositoryPort;
    private final TripPassengerRepositoryPort tripPassengerRepositoryPort;
    private final NotificationService notificationService;

    public TripServiceImpl(TripRepositoryPort tripRepositoryPort,
                           RideRequestRepositoryPort rideRequestRepositoryPort,
                           TripPassengerRepositoryPort tripPassengerRepositoryPort,
                           NotificationService notificationService) {
        this.tripRepositoryPort = tripRepositoryPort;
        this.rideRequestRepositoryPort = rideRequestRepositoryPort;
        this.tripPassengerRepositoryPort = tripPassengerRepositoryPort;
        this.notificationService = notificationService;
    }

    @Override
    public Trip createTrip(Trip trip) {
        validateDepartureTime(trip.getDepartureTime());

        if (rideRequestRepositoryPort.findPendingByPassengerId(trip.getDriverId()).isPresent()) {
            throw new IllegalStateException("У вас уже есть активная заявка пассажира. Отмените её, чтобы стать водителем.");
        }

        Optional<Trip> activeTrip = tripRepositoryPort.findActiveTripByDriverId(trip.getDriverId());
        if (activeTrip.isPresent()) {
            throw new ActiveTripAlreadyExistsException("У вас уже есть активная поездка. Отредактируйте или отмените её.");
        }

        trip.setStatus(TripStatus.CREATED);
        trip.setAvailableSeats(trip.getTotalSeats());

        Trip savedTrip = tripRepositoryPort.save(trip);

        double searchRadiusMeters = 1500.0;

        List<RideRequest> matchingRequests = rideRequestRepositoryPort.findMatchingRequestsForTrip(
                savedTrip.getOfficeId(),
                savedTrip.getRoutePath(),
                savedTrip.getDepartureTime(),
                searchRadiusMeters
        );

        for (RideRequest request : matchingRequests) {
            notificationService.sendNotification(
                    request.getPassengerId(),
                    "Найден водитель по вашему маршруту"
            );
        }

        return savedTrip;
    }

    @Override
    public Trip updateTrip(Long tripId, Trip updatedData) {
        validateDepartureTime(updatedData.getDepartureTime());

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
        Trip savedTrip = tripRepositoryPort.save(existingTrip);

        List<TripPassenger> passengers = tripPassengerRepositoryPort.findAllByTripId(tripId);
        for (TripPassenger passenger : passengers) {
            notificationService.sendNotification(
                    passenger.getPassengerId(),
                    "Внимание! Водитель отменил поездку."
            );
        }

        return savedTrip;
    }

    @Override
    public Optional<Trip> getActiveTrip(Long driverId) {
        return tripRepositoryPort.findActiveTripByDriverId(driverId);
    }

    @Override
    public List<Trip> findMatchingTripsForPassenger(Long passengerId) {
        var activeRequest = rideRequestRepositoryPort.findPendingByPassengerId(passengerId)
                .orElseThrow(() -> new IllegalStateException("Сначала создайте заявку с точкой посадки"));

        OffsetDateTime timeMin = activeRequest.getTargetTime().minusMinutes(activeRequest.getToleranceTime());
        OffsetDateTime timeMax = activeRequest.getTargetTime().plusMinutes(activeRequest.getToleranceTime());

        double searchRadiusMeters = 1500.0;

        return tripRepositoryPort.findMatchingTrips(
                activeRequest.getOfficeId(),
                timeMin,
                timeMax,
                activeRequest.getPickupLocation(),
                searchRadiusMeters
        );
    }

    private void validateDepartureTime(OffsetDateTime departureTime) {
        if (departureTime == null) {
            throw new IllegalArgumentException("Время отправления не может быть пустым");
        }

        OffsetDateTime now = OffsetDateTime.now();

        if (departureTime.isBefore(now)) {
            throw new IllegalArgumentException("Время отправления не может быть в прошлом");
        }

        if (departureTime.isAfter(now.plusHours(24))) {
            throw new IllegalArgumentException("Поездку можно запланировать не более чем на 24 часа вперед");
        }
    }
}