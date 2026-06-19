package com.carpool.controller.ride;

import com.carpool.controller.common.RequestMapper;
import com.carpool.controller.common.ResponseMapper;
import com.carpool.controller.dto.ride.RideRequestCreateRequest;
import com.carpool.controller.dto.ride.RideRequestResponse;
import com.carpool.domain.model.ride.RideRequest;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

@Component
public class RideRequestWebMapper implements RequestMapper<RideRequestCreateRequest, RideRequest>, ResponseMapper<RideRequest, RideRequestResponse> {

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    public RideRequest toDomain(RideRequestCreateRequest dto) {
        if (dto == null) return null;

        RideRequest request = new RideRequest();
        request.setOfficeId(dto.officeId());
        request.setPickupLocation(coordinatesToPoint(dto.pickupLocation()));
        request.setTargetTime(dto.targetTime());
        request.setToleranceTime(dto.toleranceTime());
        return request;
    }

    @Override
    public RideRequestResponse toDto(RideRequest domain) {
        if (domain == null) return null;

        return new RideRequestResponse(
                domain.getId(),
                domain.getPassengerId(),
                domain.getOfficeId(),
                pointToCoordinates(domain.getPickupLocation()),
                domain.getTargetTime(),
                domain.getToleranceTime(),
                domain.getStatus()
        );
    }

    private Point coordinatesToPoint(double[] coordinates) {
        if (coordinates == null || coordinates.length < 2) {
            throw new IllegalArgumentException("Локация должна содержать 2 координаты");
        }
        return geometryFactory.createPoint(new Coordinate(coordinates[0], coordinates[1]));
    }

    private double[] pointToCoordinates(Point point) {
        if (point == null) return null;
        return new double[]{point.getX(), point.getY()};
    }
}