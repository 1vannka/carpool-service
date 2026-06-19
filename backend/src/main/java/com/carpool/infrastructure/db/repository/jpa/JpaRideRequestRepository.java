package com.carpool.infrastructure.db.repository.jpa;

import com.carpool.domain.model.ride.RideRequestStatus;
import com.carpool.infrastructure.db.entity.RideRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaRideRequestRepository extends JpaRepository<RideRequestEntity, Long> {
    Optional<RideRequestEntity> findByPassengerIdAndStatus(Long passengerId, RideRequestStatus status);
}