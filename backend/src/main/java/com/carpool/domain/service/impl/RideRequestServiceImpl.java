package com.carpool.domain.service.impl;

import com.carpool.domain.model.ride.RideRequest;
import com.carpool.domain.model.ride.RideRequestStatus;
import com.carpool.domain.repository.RideRequestRepositoryPort;
import com.carpool.domain.repository.TripRepositoryPort;
import com.carpool.domain.service.RideRequestService;

import java.time.OffsetDateTime;
import java.util.Optional;

public class RideRequestServiceImpl implements RideRequestService {

    private final RideRequestRepositoryPort rideRequestRepositoryPort;
    private final TripRepositoryPort tripRepositoryPort;

    public RideRequestServiceImpl(RideRequestRepositoryPort rideRequestRepositoryPort, TripRepositoryPort tripRepositoryPort) {
        this.rideRequestRepositoryPort = rideRequestRepositoryPort;
        this.tripRepositoryPort = tripRepositoryPort;
    }

    @Override
    public RideRequest createRideRequest(RideRequest request) {
        validateTargetTime(request.getTargetTime());

        if (tripRepositoryPort.findActiveTripByDriverId(request.getPassengerId()).isPresent()) {
            throw new IllegalStateException("Вы уже создали поездку как водитель. Отмените её, чтобы стать пассажиром.");
        }

        Optional<RideRequest> existingRequest = rideRequestRepositoryPort.findPendingByPassengerId(request.getPassengerId());
        if (existingRequest.isPresent()) {
            throw new IllegalStateException("У вас уже есть активная заявка. Отмените её, чтобы создать новую.");
        }

        request.setStatus(RideRequestStatus.PENDING);

        return rideRequestRepositoryPort.save(request);
    }

    @Override
    public RideRequest cancelRideRequest(Long id) {
        RideRequest request = rideRequestRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));

        if (request.getStatus() != RideRequestStatus.PENDING) {
            throw new IllegalStateException("Можно отменить только активную заявку");
        }

        request.setStatus(RideRequestStatus.CANCELED);
        return rideRequestRepositoryPort.save(request);
    }

    @Override
    public Optional<RideRequest> getActiveRequest(Long passengerId) {
        return rideRequestRepositoryPort.findPendingByPassengerId(passengerId);
    }

    private void validateTargetTime(OffsetDateTime targetTime) {
        if (targetTime == null) {
            throw new IllegalArgumentException("Время выезда не может быть пустым");
        }

        OffsetDateTime now = OffsetDateTime.now();

        if (targetTime.isBefore(now)) {
            throw new IllegalArgumentException("Время выезда не может быть в прошлом");
        }

        if (targetTime.isAfter(now.plusHours(24))) {
            throw new IllegalArgumentException("Заявку можно создать не более чем на 24 часа вперед");
        }
    }
}