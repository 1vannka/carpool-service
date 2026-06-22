package com.carpool.controller.trip;

import com.carpool.controller.dto.trip.TripPassengerDetailedResponse;
import com.carpool.controller.dto.trip.TripPassengerResponse;
import com.carpool.domain.model.trip.TripPassenger;
import com.carpool.domain.model.user.User;
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

     public TripPassengerDetailedResponse toDetailedDto(TripPassenger domain, User user) {
         if (domain == null) return null;

         String firstName = user.getFirstName();
         String lastName = user.getLastName();

         return new TripPassengerDetailedResponse(
                 domain.getTripId(),
                 firstName,
                 lastName,
                 domain.getPassengerId(),
                 domain.getStatus()
         );
     }
}