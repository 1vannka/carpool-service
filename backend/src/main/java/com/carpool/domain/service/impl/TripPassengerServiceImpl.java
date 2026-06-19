package com.carpool.domain.service.impl;

import com.carpool.domain.model.ride.RideRequest;
import com.carpool.domain.model.trip.BookingStatus;
import com.carpool.domain.model.trip.Trip;
import com.carpool.domain.model.trip.TripPassenger;
import com.carpool.domain.model.trip.TripStatus;
import com.carpool.domain.repository.RideRequestRepositoryPort;
import com.carpool.domain.repository.TripPassengerRepositoryPort;
import com.carpool.domain.repository.TripRepositoryPort;
import com.carpool.domain.service.TripPassengerService;

public class TripPassengerServiceImpl implements TripPassengerService {

    private final TripPassengerRepositoryPort tripPassengerRepositoryPort;
    private final TripRepositoryPort tripRepositoryPort;
    private final RideRequestRepositoryPort rideRequestRepositoryPort;

    public TripPassengerServiceImpl(TripPassengerRepositoryPort tripPassengerRepositoryPort, TripRepositoryPort tripRepositoryPort,
                                    RideRequestRepositoryPort rideRequestRepositoryPort) {
        this.tripPassengerRepositoryPort = tripPassengerRepositoryPort;
        this.tripRepositoryPort = tripRepositoryPort;
        this.rideRequestRepositoryPort = rideRequestRepositoryPort;
    }

    @Override
    public TripPassenger requestToJoin(Long tripId, Long passengerId) {
        Trip trip = tripRepositoryPort.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Поездка не найдена"));

        if (trip.getStatus() != TripStatus.CREATED) {
            throw new IllegalStateException("К этой поездке уже нельзя присоединиться");
        }

        if (trip.getAvailableSeats() <= 0) {
            throw new IllegalStateException("Свободных мест нет");
        }

        if (trip.getDriverId().equals(passengerId)) {
            throw new IllegalStateException("Вы не можете быть пассажиром в своей же поездке");
        }

        RideRequest activeRequest = rideRequestRepositoryPort.findPendingByPassengerId(passengerId)
                .orElseThrow(() -> new IllegalStateException("Сначала создайте заявку с точкой посадки"));

        TripPassenger request = new TripPassenger(tripId, passengerId, BookingStatus.WAITING_APPROVAL);
        return tripPassengerRepositoryPort.save(request);
    }

    @Override
    public TripPassenger approvePassenger(Long tripId, Long passengerId, Long driverId) {
        Trip trip = validateDriverAndGetTrip(tripId, driverId);

        TripPassenger request = tripPassengerRepositoryPort.findByIds(tripId, passengerId)
                .orElseThrow(() -> new IllegalArgumentException("Заявка пассажира не найдена"));

        if (request.getStatus() != BookingStatus.WAITING_APPROVAL) {
            throw new IllegalStateException("Заявка уже обработана");
        }

        if (trip.getAvailableSeats() <= 0) {
            throw new IllegalStateException("Свободных мест нет");
        }

        request.setStatus(BookingStatus.CONFIRMED);
        TripPassenger savedRequest = tripPassengerRepositoryPort.save(request);

        trip.setAvailableSeats(trip.getAvailableSeats() - 1);
        tripRepositoryPort.save(trip);

        return savedRequest;
    }

    @Override
    public void rejectPassenger(Long tripId, Long passengerId, Long driverId) {
        validateDriverAndGetTrip(tripId, driverId);

        TripPassenger request = tripPassengerRepositoryPort.findByIds(tripId, passengerId)
                .orElseThrow(() -> new IllegalArgumentException("Заявка пассажира не найдена"));

        tripPassengerRepositoryPort.delete(tripId, passengerId);
    }

    private Trip validateDriverAndGetTrip(Long tripId, Long driverId) {
        Trip trip = tripRepositoryPort.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Поездка не найдена"));

        if (!trip.getDriverId().equals(driverId)) {
            throw new SecurityException("У вас нет прав на управление пассажирами этой поездки");
        }
        return trip;
    }
}