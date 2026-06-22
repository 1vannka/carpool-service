package com.carpool.controller.trip;

import com.carpool.controller.common.BiResponseMapper;
import com.carpool.controller.common.RequestMapper;
import com.carpool.controller.common.ResponseMapper;
import com.carpool.controller.dto.trip.TripCreateRequest;
import com.carpool.controller.dto.trip.TripDetailedResponse;
import com.carpool.controller.dto.trip.TripResponse;
import com.carpool.controller.dto.trip.TripUpdateRequest;
import com.carpool.domain.model.trip.Trip;
import com.carpool.domain.model.user.User;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

@Component
public class TripWebMapper implements RequestMapper<TripCreateRequest, Trip>, ResponseMapper<Trip, TripResponse>, BiResponseMapper<Trip, User, TripDetailedResponse> {

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    public Trip toDomain(TripCreateRequest dto) {
        if (dto == null) return null;

        Trip trip = new Trip();
        trip.setOfficeId(dto.officeId());
        trip.setDepartureTime(dto.departureTime());
        trip.setEstimatedDuration(dto.estimatedDuration());
        trip.setTotalSeats(dto.totalSeats());
        trip.setCarModel(dto.carModel());
        trip.setCarColor(dto.carColor());
        trip.setCarPlate(dto.carPlate());
        trip.setRoutePath(coordinatesToLineString(dto.routePath()));

        return trip;
    }

    public Trip toDomain(TripUpdateRequest dto) {
        if (dto == null) return null;

        Trip trip = new Trip();
        trip.setDepartureTime(dto.departureTime());
        trip.setEstimatedDuration(dto.estimatedDuration());
        trip.setTotalSeats(dto.totalSeats());
        trip.setCarModel(dto.carModel());
        trip.setCarColor(dto.carColor());
        trip.setCarPlate(dto.carPlate());
        trip.setRoutePath(coordinatesToLineString(dto.routePath()));

        return trip;
    }

    @Override
    public TripResponse toDto(Trip domain) {
        if (domain == null) return null;

        return new TripResponse(
                domain.getId(),
                domain.getOfficeId(),
                domain.getDepartureTime(),
                domain.getEstimatedDuration(),
                domain.getTotalSeats(),
                domain.getAvailableSeats(),
                domain.getCarModel(),
                domain.getCarColor(),
                domain.getCarPlate(),
                domain.getStatus(),
                lineStringToCoordinates(domain.getRoutePath())
        );
    }

    @Override
    public TripDetailedResponse toDetailedDto(Trip domain, User user){
        if (domain == null) return null;

        String firstName = user.getFirstName();
        String lastName = user.getLastName();

        return new TripDetailedResponse(
                domain.getId(),
                domain.getOfficeId(),
                firstName,
                lastName,
                domain.getDepartureTime(),
                domain.getEstimatedDuration(),
                domain.getTotalSeats(),
                domain.getAvailableSeats(),
                domain.getCarModel(),
                domain.getCarColor(),
                domain.getCarPlate(),
                domain.getStatus(),
                lineStringToCoordinates(domain.getRoutePath())
        );
    }

    private LineString coordinatesToLineString(double[][] rawPath) {
        if (rawPath == null || rawPath.length < 2) {
            throw new IllegalArgumentException("Маршрут должен содержать минимум 2 точки");
        }
        Coordinate[] coordinates = new Coordinate[rawPath.length];
        for (int i = 0; i < rawPath.length; i++) {
            coordinates[i] = new Coordinate(rawPath[i][0], rawPath[i][1]);
        }
        return geometryFactory.createLineString(coordinates);
    }

    private double[][] lineStringToCoordinates(LineString lineString) {
        if (lineString == null) return null;

        Coordinate[] coordinates = lineString.getCoordinates();
        double[][] rawPath = new double[coordinates.length][2];
        for (int i = 0; i < coordinates.length; i++) {
            rawPath[i][0] = coordinates[i].getX();
            rawPath[i][1] = coordinates[i].getY();
        }
        return rawPath;
    }
}