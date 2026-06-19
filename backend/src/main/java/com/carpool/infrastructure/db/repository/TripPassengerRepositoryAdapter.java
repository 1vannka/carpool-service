package com.carpool.infrastructure.db.repository;

import com.carpool.domain.model.trip.TripPassenger;
import com.carpool.domain.repository.TripPassengerRepositoryPort;
import com.carpool.infrastructure.db.entity.TripPassengerEntity;
import com.carpool.infrastructure.db.entity.TripPassengerId;
import com.carpool.infrastructure.db.repository.jpa.JpaTripPassengerRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TripPassengerRepositoryAdapter implements TripPassengerRepositoryPort {

    private final JpaTripPassengerRepository jpaRepository;

    public TripPassengerRepositoryAdapter(JpaTripPassengerRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TripPassenger save(TripPassenger tripPassenger) {
        TripPassengerId id = new TripPassengerId(tripPassenger.getTripId(), tripPassenger.getPassengerId());
        TripPassengerEntity entity = new TripPassengerEntity(id, tripPassenger.getStatus());

        TripPassengerEntity savedEntity = jpaRepository.save(entity);

        return new TripPassenger(
                savedEntity.getId().getTripId(),
                savedEntity.getId().getPassengerId(),
                savedEntity.getStatus()
        );
    }

    @Override
    public Optional<TripPassenger> findByIds(Long tripId, Long passengerId) {
        TripPassengerId id = new TripPassengerId(tripId, passengerId);
        return jpaRepository.findById(id)
                .map(entity -> new TripPassenger(
                        entity.getId().getTripId(),
                        entity.getId().getPassengerId(),
                        entity.getStatus()
                ));
    }

    @Override
    public void delete(Long tripId, Long passengerId) {
        TripPassengerId id = new TripPassengerId(tripId, passengerId);
        jpaRepository.deleteById(id);
    }
}