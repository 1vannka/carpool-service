package com.carpool.controller.office;

import com.carpool.controller.common.RequestMapper;
import com.carpool.controller.common.ResponseMapper;
import com.carpool.controller.dto.office.OfficeCreateRequest;
import com.carpool.controller.dto.office.OfficeResponse;
import com.carpool.controller.dto.office.OfficeUpdateRequest;
import com.carpool.domain.model.office.Office;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

@Component
public class OfficeWebMapper implements RequestMapper<OfficeCreateRequest, Office>, ResponseMapper<Office, OfficeResponse> {

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    public Office toDomain(OfficeCreateRequest dto) {
        if (dto == null) return null;

        Office office = new Office();
        office.setName(dto.name());
        office.setCity(dto.city());
        office.setAddress(dto.address());
        office.setLocation(coordinatesToPoint(dto.location()));

        return office;
    }

    public Office toDomain(OfficeUpdateRequest dto) {
        if (dto == null) return null;

        Office office = new Office();
        office.setName(dto.name());
        office.setCity(dto.city());
        office.setAddress(dto.address());
        office.setLocation(coordinatesToPoint(dto.location()));

        return office;
    }

    @Override
    public OfficeResponse toDto(Office domain) {
        if (domain == null) return null;

        return new OfficeResponse(
                domain.getId(),
                domain.getName(),
                domain.getCity(),
                domain.getAddress(),
                pointToCoordinates(domain.getLocation())
        );
    }

    private Point coordinatesToPoint(double[] coordinates) {
        if (coordinates == null || coordinates.length < 2) {
            throw new IllegalArgumentException("Локация должна содержать 2 координаты (долгота, широта)");
        }
        return geometryFactory.createPoint(new Coordinate(coordinates[0], coordinates[1]));
    }

    private double[] pointToCoordinates(Point point) {
        if (point == null) return null;
        return new double[]{point.getX(), point.getY()};
    }
}