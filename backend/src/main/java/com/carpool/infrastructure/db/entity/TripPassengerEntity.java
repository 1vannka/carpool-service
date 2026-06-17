package com.carpool.infrastructure.db.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "trip_passengers")
public class TripPassengerEntity {

    @EmbeddedId
    private TripPassengerId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tripId")
    @JoinColumn(name = "trip_id")
    private TripEntity trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("passengerId")
    @JoinColumn(name = "passenger_id")
    private UserEntity passenger;

    @Column(nullable = false)
    private String status = "WAITING_APPROVAL";

    public TripPassengerEntity() {}

    public TripPassengerId getId() {
        return id;
    }

    public void setId(TripPassengerId id) {
        this.id = id;
    }

    public TripEntity getTrip() {
        return trip;
    }

    public void setTrip(TripEntity trip) {
        this.trip = trip;
    }

    public UserEntity getPassenger() {
        return passenger;
    }

    public void setPassenger(UserEntity passenger) {
        this.passenger = passenger;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}