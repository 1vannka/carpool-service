package com.carpool.infrastructure.db.mapper;

import com.carpool.domain.model.trip.Trip;
import com.carpool.infrastructure.db.entity.TripEntity;
import com.carpool.infrastructure.db.common.Mapper;
import org.springframework.stereotype.Component;

@Component
public class TripMapper implements Mapper<TripEntity, Trip> {

    @Override
    public Trip toDomain(TripEntity entity) {
        if (entity == null) return null;

        Trip trip = new Trip();
        trip.setId(entity.getId());
        trip.setDriverId(entity.getDriverId());
        trip.setOfficeId(entity.getOfficeId());
        trip.setDepartureTime(entity.getDepartureTime());
        trip.setEstimatedDuration(entity.getEstimatedDuration());
        trip.setTotalSeats(entity.getTotalSeats());
        trip.setAvailableSeats(entity.getAvailableSeats());
        trip.setCarModel(entity.getCarModel());
        trip.setCarColor(entity.getCarColor());
        trip.setCarPlate(entity.getCarPlate());
        trip.setRoutePath(entity.getRoutePath());
        trip.setVersion(entity.getVersion());
        trip.setStatus(entity.getStatus());
        trip.setStartLocation(entity.getStartLocation());

        return trip;
    }

    @Override
    public TripEntity toEntity(Trip domain) {
        if (domain == null) return null;

        TripEntity entity = new TripEntity();
        entity.setId(domain.getId());
        entity.setDriverId(domain.getDriverId());
        entity.setOfficeId(domain.getOfficeId());
        entity.setDepartureTime(domain.getDepartureTime());
        entity.setEstimatedDuration(domain.getEstimatedDuration());
        entity.setTotalSeats(domain.getTotalSeats());
        entity.setAvailableSeats(domain.getAvailableSeats());
        entity.setCarModel(domain.getCarModel());
        entity.setCarColor(domain.getCarColor());
        entity.setCarPlate(domain.getCarPlate());
        entity.setRoutePath(domain.getRoutePath());
        entity.setVersion(domain.getVersion());
        entity.setStatus(domain.getStatus());
        entity.setStartLocation(domain.getStartLocation());

        return entity;
    }
}