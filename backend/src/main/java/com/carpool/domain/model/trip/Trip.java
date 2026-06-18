package com.carpool.domain.model.trip;

import org.locationtech.jts.geom.LineString;
import java.time.OffsetDateTime;

public class Trip {
    private Long id;
    private Long driverId;
    private Long officeId;
    private OffsetDateTime departureTime;
    private Integer estimatedDuration;
    private Integer totalSeats;
    private Integer availableSeats;
    private String carModel;
    private String carColor;
    private String carPlate;
    private LineString routePath;
    private Long version;
    private TripStatus status;

    public Trip() {
    }

    public Trip(Long id, Long driverId, Long officeId, OffsetDateTime departureTime, Integer estimatedDuration, Integer totalSeats, Integer availableSeats, String carModel, String carColor, String carPlate, LineString routePath, Long version, TripStatus status) {
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
}