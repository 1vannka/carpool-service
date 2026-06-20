package com.carpool.infrastructure.db.repository.jpa;

import com.carpool.infrastructure.db.entity.TripPassengerEntity;
import com.carpool.infrastructure.db.entity.TripPassengerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface JpaTripPassengerRepository extends JpaRepository<TripPassengerEntity, TripPassengerId> {
    @Modifying
    @Query("UPDATE TripPassengerEntity p SET p.status = 'REJECTED' WHERE p.status = 'WAITING_APPROVAL' AND p.id.tripId IN (SELECT t.id FROM TripEntity t WHERE t.departureTime <= :now)")
    int updateExpiredPassengerRequests(@Param("now") OffsetDateTime now);
    List<TripPassengerEntity> findByIdTripId(Long tripId);

    @Query(value = "SELECT p.passenger_id FROM trip_passengers p JOIN trips t ON p.trip_id = t.id WHERE p.status = 'WAITING_APPROVAL' AND t.departure_time <= :now", nativeQuery = true)
    List<Long> findPassengerIdsForExpiredApprovals(@Param("now") OffsetDateTime now);
}