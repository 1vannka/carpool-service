package com.carpool.infrastructure.db.repository;

import com.carpool.domain.model.ride.RideRequest;
import com.carpool.domain.model.ride.RideRequestStatus;
import com.carpool.domain.repository.RideRequestRepositoryPort;
import com.carpool.infrastructure.db.mapper.RideRequestDbMapper;
import com.carpool.infrastructure.db.repository.jpa.JpaRideRequestRepository;
import org.locationtech.jts.geom.LineString;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class RideRequestRepositoryAdapter implements RideRequestRepositoryPort {

    private final JpaRideRequestRepository jpaRepository;
    private final RideRequestDbMapper mapper;

    public RideRequestRepositoryAdapter(JpaRideRequestRepository jpaRepository, RideRequestDbMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public RideRequest save(RideRequest rideRequest) {
        var entity = mapper.toEntity(rideRequest);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<RideRequest> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<RideRequest> findPendingByPassengerId(Long passengerId) {
        return jpaRepository.findByPassengerIdAndStatus(passengerId, RideRequestStatus.PENDING)
                .map(mapper::toDomain);
    }

    @Override
    public List<RideRequest> findMatchingRequestsForTrip(Long officeId,
                                                         LineString routePath,
                                                         OffsetDateTime tripTime,
                                                         Integer estimatedDuration,
                                                         double radiusMeters) {
        return jpaRepository.findMatchingRequestsForTrip(officeId, routePath, tripTime, estimatedDuration, radiusMeters)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}