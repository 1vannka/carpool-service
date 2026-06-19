package com.carpool.domain.model.trip;

import java.util.Objects;

public class TripPassenger {

    private Long tripId;
    private Long passengerId;
    private BookingStatus status;

    public TripPassenger() {
    }

    public TripPassenger(Long tripId, Long passengerId, BookingStatus status) {
        this.tripId = tripId;
        this.passengerId = passengerId;
        this.status = status;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripPassenger that = (TripPassenger) o;
        return Objects.equals(tripId, that.tripId) &&
                Objects.equals(passengerId, that.passengerId) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId, passengerId, status);
    }
}