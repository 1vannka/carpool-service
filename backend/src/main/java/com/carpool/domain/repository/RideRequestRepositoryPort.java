package com.carpool.domain.repository;

import com.carpool.domain.model.ride.RideRequest;
import org.locationtech.jts.geom.LineString;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface RideRequestRepositoryPort {
    RideRequest save(RideRequest rideRequest);
    Optional<RideRequest> findById(Long id);
    Optional<RideRequest> findPendingByPassengerId(Long passengerId);
    List<RideRequest> findMatchingRequestsForTrip(
            Long officeId,
            LineString routePath,
            OffsetDateTime tripTime,
            Integer estimatedDuration,
            double radiusMeters
    );
}