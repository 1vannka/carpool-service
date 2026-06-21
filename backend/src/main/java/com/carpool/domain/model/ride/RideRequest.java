package com.carpool.domain.model.ride;

import org.locationtech.jts.geom.Point;
import java.time.OffsetDateTime;
import java.util.Objects;

public class RideRequest {

    private Long id;
    private Long passengerId;
    private Long officeId;
    private Point pickupLocation;
    private OffsetDateTime targetTime;
    private Integer toleranceTime;
    private RideRequestStatus status;

    public RideRequest() {
    }

    public RideRequest(Long id, Long passengerId, Long officeId, Point pickupLocation, OffsetDateTime targetTime, Integer toleranceTime, RideRequestStatus status) {
        this.id = id;
        this.passengerId = passengerId;
        this.officeId = officeId;
        this.pickupLocation = pickupLocation;
        this.targetTime = targetTime;
        this.toleranceTime = toleranceTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public Long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    public Point getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Point pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public OffsetDateTime getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(OffsetDateTime targetTime) {
        this.targetTime = targetTime;
    }

    public Integer getToleranceTime() {
        return toleranceTime;
    }

    public void setToleranceTime(Integer toleranceTime) {
        this.toleranceTime = toleranceTime;
    }

    public RideRequestStatus getStatus() {
        return status;
    }

    public void setStatus(RideRequestStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RideRequest that = (RideRequest) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(passengerId, that.passengerId) &&
                Objects.equals(officeId, that.officeId) &&
                Objects.equals(pickupLocation, that.pickupLocation) &&
                Objects.equals(targetTime, that.targetTime) &&
                Objects.equals(toleranceTime, that.toleranceTime) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, passengerId, officeId, pickupLocation, targetTime, toleranceTime, status);
    }
}