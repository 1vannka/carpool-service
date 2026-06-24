package com.carpool.infrastructure.db.entity;

import com.carpool.domain.model.trip.TripStatus;
import jakarta.persistence.*;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import java.time.OffsetDateTime;

@Entity
@Table(name = "trips")
public class TripEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "driver_id", nullable = false)
    private Long driverId;

    @Column(name = "office_id", nullable = false)
    private Long officeId;

    @Column(name = "departure_time", nullable = false)
    private OffsetDateTime departureTime;

    @Column(name = "estimated_duration", nullable = false)
    private Integer estimatedDuration;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Column(name = "car_model", nullable = false)
    private String carModel;

    @Column(name = "car_color", nullable = false)
    private String carColor;

    @Column(name = "car_plate", nullable = false)
    private String carPlate;

    @Column(name = "route_path", nullable = false, columnDefinition = "geometry(LineString,4326)")
    private LineString routePath;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TripStatus status;

    @Column(name = "start_location", nullable = false, columnDefinition = "geometry(Point,4326)")
    private Point startLocation;

    public TripEntity() {
    }

    public TripEntity(Long id, Long driverId, Long officeId, OffsetDateTime departureTime, Integer estimatedDuration, Integer totalSeats, Integer availableSeats, String carModel, String carColor, String carPlate, LineString routePath, Long version, TripStatus status, Point startLocation) {
        this.id = id;
        this.driverId = driverId;
        this.officeId = officeId;
        this.departureTime = departureTime;
        this.estimatedDuration = estimatedDuration;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.carModel = carModel;
        this.carColor = carColor;
        this.carPlate = carPlate;
        this.routePath = routePath;
        this.version = version;
        this.status = status;
        this.startLocation = startLocation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    public OffsetDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(OffsetDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public Integer getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(Integer estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public String getCarPlate() {
        return carPlate;
    }

    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
    }

    public LineString getRoutePath() {
        return routePath;
    }

    public void setRoutePath(LineString routePath) {
        this.routePath = routePath;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }

    public Point getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Point startLocation) {
        this.startLocation = startLocation;
    }
}