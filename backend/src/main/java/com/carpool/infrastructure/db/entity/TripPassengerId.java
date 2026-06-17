package com.carpool.infrastructure.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TripPassengerId implements Serializable {

    @Column(name = "trip_id")
    private Long tripId;

    @Column(name = "passenger_id")
    private Long passengerId;

    public TripPassengerId() {}

    public TripPassengerId(Long tripId, Long passengerId) {
        this.tripId = tripId;
        this.passengerId = passengerId;
    }

    public Long getTripId() { return tripId; }

    public void setTripId(Long tripId) { this.tripId = tripId; }

    public Long getPassengerId() { return passengerId; }

    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripPassengerId that = (TripPassengerId) o;
        return Objects.equals(tripId, that.tripId) && Objects.equals(passengerId, that.passengerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId, passengerId);
    }
}