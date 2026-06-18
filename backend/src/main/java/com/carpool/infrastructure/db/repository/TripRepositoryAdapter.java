package com.carpool.infrastructure.db.repository;

import com.carpool.domain.model.trip.Trip;
import com.carpool.domain.model.trip.TripStatus;
import com.carpool.domain.repository.TripRepositoryPort;
import com.carpool.infrastructure.db.repository.jpa.JpaTripRepository;
import com.carpool.infrastructure.db.mapper.TripMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TripRepositoryAdapter implements TripRepositoryPort {

    private final JpaTripRepository jpaTripRepository;
    private final TripMapper tripMapper;

    public TripRepositoryAdapter(JpaTripRepository jpaTripRepository, TripMapper tripMapper) {
        this.jpaTripRepository = jpaTripRepository;
        this.tripMapper = tripMapper;
    }

    @Override
    public Trip save(Trip trip) {
        return tripMapper.toDomain(jpaTripRepository.save(tripMapper.toEntity(trip)));
    }

    @Override
    public Optional<Trip> findActiveTripByDriverId(Long driverId) {
        return jpaTripRepository.findByDriverIdAndStatusIn(
                        driverId,
                        List.of(TripStatus.CREATED, TripStatus.IN_PROGRESS)
                )
                .map(tripMapper::toDomain);
    }

    @Override
    public Optional<Trip> findById(Long id) {
        return jpaTripRepository.findById(id).map(tripMapper::toDomain);
    }
}