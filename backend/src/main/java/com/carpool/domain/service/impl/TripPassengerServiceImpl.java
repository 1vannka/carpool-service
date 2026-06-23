package com.carpool.domain.service.impl;

import com.carpool.domain.model.ride.RideRequest;
import com.carpool.domain.model.trip.BookingStatus;
import com.carpool.domain.model.trip.Trip;
import com.carpool.domain.model.trip.TripPassenger;
import com.carpool.domain.model.trip.TripStatus;
import com.carpool.domain.repository.RideRequestRepositoryPort;
import com.carpool.domain.repository.TripPassengerRepositoryPort;
import com.carpool.domain.repository.TripRepositoryPort;
import com.carpool.domain.service.NotificationService;
import com.carpool.domain.service.TripPassengerService;

import java.util.List;
import java.util.Optional;

public class TripPassengerServiceImpl implements TripPassengerService {

    private final TripPassengerRepositoryPort tripPassengerRepositoryPort;
    private final TripRepositoryPort tripRepositoryPort;
    private final RideRequestRepositoryPort rideRequestRepositoryPort;
    private final NotificationService notificationService;

    public TripPassengerServiceImpl(TripPassengerRepositoryPort tripPassengerRepositoryPort, TripRepositoryPort tripRepositoryPort,
                                    RideRequestRepositoryPort rideRequestRepositoryPort, NotificationService notificationService) {
        this.tripPassengerRepositoryPort = tripPassengerRepositoryPort;
        this.tripRepositoryPort = tripRepositoryPort;
        this.rideRequestRepositoryPort = rideRequestRepositoryPort;
        this.notificationService = notificationService;
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

        if (tripPassengerRepositoryPort.findByIds(tripId, passengerId).isPresent()) {
            throw new IllegalStateException("Вы уже отправляли заявку на эту поездку");
        }

        RideRequest activeRequest = rideRequestRepositoryPort.findPendingByPassengerId(passengerId)
                .orElseThrow(() -> new IllegalStateException("Сначала создайте заявку с точкой посадки"));

        TripPassenger request = new TripPassenger(tripId, passengerId, BookingStatus.WAITING_APPROVAL);
        TripPassenger savedRequest = tripPassengerRepositoryPort.save(request);

        notificationService.sendNotification(trip.getDriverId(), "NEW_PASSENGER_REQUEST", tripId, passengerId, "У вас новая заявка на присоединение к поездке");

        return savedRequest;
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

        notificationService.sendNotification(passengerId, "PASSENGER_APPROVED", tripId, passengerId, "Водитель одобрил вашу заявку на поездку");

        return savedRequest;
    }

    @Override
    public void rejectPassenger(Long tripId, Long passengerId, Long driverId) {
        validateDriverAndGetTrip(tripId, driverId);

        TripPassenger request = tripPassengerRepositoryPort.findByIds(tripId, passengerId)
                .orElseThrow(() -> new IllegalArgumentException("Заявка пассажира не найдена"));

        if (request.getStatus() == BookingStatus.REJECTED) {
            throw new IllegalStateException("Заявка уже отклонена");
        }

        request.setStatus(BookingStatus.REJECTED);
        tripPassengerRepositoryPort.save(request);

        notificationService.sendNotification(passengerId, "PASSENGER_REJECTED", tripId, passengerId, "Водитель отклонил вашу заявку");
    }

    @Override
    public void cancelPassengerRequest(Long tripId, Long passengerId) {
        TripPassenger request = tripPassengerRepositoryPort.findByIds(tripId, passengerId)
                .orElseThrow(() -> new IllegalArgumentException("Заявка пассажира не найдена"));

        if (request.getStatus() == BookingStatus.REJECTED) {
            throw new IllegalStateException("Ваша заявка уже отклонена водителем, ее нельзя отменить");
        }

        Trip trip = tripRepositoryPort.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Поездка не найдена"));

        if (request.getStatus() == BookingStatus.CONFIRMED) {
            trip.setAvailableSeats(trip.getAvailableSeats() + 1);
            tripRepositoryPort.save(trip);

            notificationService.sendNotification(trip.getDriverId(), "PASSENGER_LEFT_TRIP", tripId, passengerId, "Пассажир отменил поездку. У вас освободилось 1 место");
        }
        else if (request.getStatus() == BookingStatus.WAITING_APPROVAL) {
            notificationService.sendNotification(trip.getDriverId(), "PASSENGER_CANCELED_REQUEST", tripId, passengerId, "Пассажир отменил свою заявку");
        }

        tripPassengerRepositoryPort.delete(tripId, passengerId);
    }

    @Override
    public List<TripPassenger> getPassengersByTripId(Long tripId) {
        tripRepositoryPort.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Поездка не найдена"));

        return tripPassengerRepositoryPort.findAllByTripId(tripId);
    }

    @Override
    public Optional<TripPassenger> getPassengerStatus(Long tripId, Long passengerId) {
        return tripPassengerRepositoryPort.findByIds(tripId, passengerId);
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