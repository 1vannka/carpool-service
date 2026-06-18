package com.carpool.infrastructure.db.repository.jpa;

import com.carpool.domain.model.trip.TripStatus;
import com.carpool.infrastructure.db.entity.TripEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface JpaTripRepository extends JpaRepository<TripEntity, Long> {
    Optional<TripEntity> findByDriverIdAndStatusIn(Long driverId, List<TripStatus> statuses);
}