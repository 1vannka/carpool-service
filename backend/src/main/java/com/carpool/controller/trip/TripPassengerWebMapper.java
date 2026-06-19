package com.carpool.controller.trip;

import com.carpool.controller.dto.trip.TripPassengerResponse;
import com.carpool.domain.model.trip.TripPassenger;
import org.springframework.stereotype.Component;

@Component
public class TripPassengerWebMapper {

    public TripPassengerResponse toDto(TripPassenger domain) {
        if (domain == null) {
            return null;
        }
        return new TripPassengerResponse(
                domain.getTripId(),
                domain.getPassengerId(),
                domain.getStatus()
        );
    }
}