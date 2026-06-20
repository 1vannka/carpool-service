package com.carpool.infrastructure.db.repository.jpa;

import com.carpool.domain.model.ride.RideRequestStatus;
import com.carpool.infrastructure.db.entity.RideRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaRideRequestRepository extends JpaRepository<RideRequestEntity, Long> {
    Optional<RideRequestEntity> findByPassengerIdAndStatus(Long passengerId, RideRequestStatus status);

    @Query(value = """
        SELECT r.* FROM ride_requests r
        WHERE r.office_id = :officeId
          AND r.status = 'PENDING'
          AND :tripTime >= (r.target_time - (r.tolerance_time * interval '1 minute'))
          AND :tripTime <= (r.target_time + (r.tolerance_time * interval '1 minute'))
          AND ST_DWithin(CAST(r.pickup_location AS geography), CAST(:routePath AS geography), :radiusMeters)
        """, nativeQuery = true)
    List<RideRequestEntity> findMatchingRequestsForTrip(
            @Param("officeId") Long officeId,
            @Param("routePath") org.locationtech.jts.geom.LineString routePath,
            @Param("tripTime") OffsetDateTime tripTime,
            @Param("radiusMeters") double radiusMeters
    );
}