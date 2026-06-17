package com.carpool.infrastructure.db.entity;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ride_requests")
public class RideRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private UserEntity passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id", nullable = false)
    private OfficeEntity office;

    @Column(name = "pickup_location", nullable = false, columnDefinition = "geometry(Point,4326)")
    private Point pickupLocation;

    @Column(name = "target_time", nullable = false)
    private OffsetDateTime targetTime;

    @Column(name = "tolerance_time", nullable = false)
    private Integer toleranceTime;

    @Column(nullable = false)
    private String status;

    public RideRequestEntity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getPassenger() {
        return passenger;
    }

    public void setPassenger(UserEntity passenger) {
        this.passenger = passenger;
    }

    public OfficeEntity getOffice() {
        return office;
    }

    public void setOffice(OfficeEntity office) {
        this.office = office;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}