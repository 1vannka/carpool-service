package com.carpool.infrastructure.db.mapper;

import com.carpool.domain.model.ride.RideRequest;
import com.carpool.infrastructure.db.common.Mapper;
import com.carpool.infrastructure.db.entity.RideRequestEntity;
import org.springframework.stereotype.Component;

@Component
public class RideRequestDbMapper implements Mapper<RideRequestEntity, RideRequest> {

    @Override
    public RideRequest toDomain(RideRequestEntity entity) {
        if (entity == null) {
            return null;
        }
        return new RideRequest(
                entity.getId(),
                entity.getPassengerId(),
                entity.getOfficeId(),
                entity.getPickupLocation(),
                entity.getTargetTime(),
                entity.getToleranceTime(),
                entity.getStatus()
        );
    }

    @Override
    public RideRequestEntity toEntity(RideRequest domain) {
        if (domain == null) {
            return null;
        }
        RideRequestEntity entity = new RideRequestEntity();
        entity.setId(domain.getId());
        entity.setPassengerId(domain.getPassengerId());
        entity.setOfficeId(domain.getOfficeId());
        entity.setPickupLocation(domain.getPickupLocation());
        entity.setTargetTime(domain.getTargetTime());
        entity.setToleranceTime(domain.getToleranceTime());
        entity.setStatus(domain.getStatus());
        return entity;
    }
}