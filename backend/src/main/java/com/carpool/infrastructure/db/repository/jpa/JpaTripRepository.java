package com.carpool.infrastructure.db.repository.jpa;

import com.carpool.domain.model.trip.TripStatus;
import com.carpool.infrastructure.db.entity.TripEntity;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaTripRepository extends JpaRepository<TripEntity, Long> {

    Optional<TripEntity> findByDriverIdAndStatusIn(Long driverId, List<TripStatus> statuses);

    @Query(value = """
        SELECT t.* FROM trips t
        WHERE t.office_id = :officeId
          AND t.available_seats > 0
          AND t.status = 'CREATED'
          AND t.departure_time >= :timeMin
          AND t.departure_time <= :timeMax
          AND ST_DWithin(CAST(t.route_path AS geography), CAST(:pickupLocation AS geography), :radiusMeters)
        """, nativeQuery = true)
    List<TripEntity> findMatchingTrips(
            @Param("officeId") Long officeId,
            @Param("timeMin") OffsetDateTime timeMin,
            @Param("timeMax") OffsetDateTime timeMax,
            @Param("pickupLocation") Point pickupLocation,
            @Param("radiusMeters") double radiusMeters
    );

    @Modifying
    @Query("UPDATE TripEntity t SET t.status = 'IN_PROGRESS' WHERE t.status = 'CREATED' AND t.departureTime <= :now")
    int updateStartedTrips(@Param("now") OffsetDateTime now);

    @Modifying
    @Query(value = "UPDATE trips SET status = 'COMPLETED' WHERE status = 'IN_PROGRESS' AND departure_time + ((estimated_duration + 60) * interval '1 minute') <= :now", nativeQuery = true)
    int updateCompletedTrips(@Param("now") OffsetDateTime now);
}