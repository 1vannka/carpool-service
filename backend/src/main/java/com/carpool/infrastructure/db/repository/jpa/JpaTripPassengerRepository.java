package com.carpool.infrastructure.db.repository.jpa;

import com.carpool.infrastructure.db.entity.TripPassengerEntity;
import com.carpool.infrastructure.db.entity.TripPassengerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTripPassengerRepository extends JpaRepository<TripPassengerEntity, TripPassengerId> {
}