package com.carpool.infrastructure.db.entity;

import com.carpool.domain.model.trip.BookingStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "trip_passengers")
public class TripPassengerEntity {

    @EmbeddedId
    private TripPassengerId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    public TripPassengerEntity() {}

    public TripPassengerEntity(TripPassengerId id, BookingStatus status) {
        this.id = id;
        this.status = status;
    }

    public TripPassengerId getId() {
        return id;
    }

    public void setId(TripPassengerId id) {
        this.id = id;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}