package com.carpool.controller.trip;

import com.carpool.controller.dto.trip.TripPassengerDetailedResponse;
import com.carpool.controller.dto.trip.TripPassengerResponse;
import com.carpool.domain.model.ride.RideRequest;
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

    public TripPassengerDetailedResponse toDetailedDto(TripPassenger domain, User user, RideRequest rideRequest) {
        if (domain == null) return null;

        String firstName = user != null ? user.getFirstName() : "Удаленный";
        String lastName = user != null ? user.getLastName() : "Пользователь";
        String telegramAlias = "N/A";
        String vkAlias = "N/A";

        if (user != null) {
            try { telegramAlias = user.getTelegramAlias(); } catch (IllegalArgumentException e) {}
            try { vkAlias = user.getVkAlias(); } catch (IllegalArgumentException e) {}
        }

        double[] pickupLocation = null;
        if (rideRequest != null && rideRequest.getPickupLocation() != null) {
            pickupLocation = new double[]{
                    rideRequest.getPickupLocation().getX(),
                    rideRequest.getPickupLocation().getY()
            };
        }

        return new TripPassengerDetailedResponse(
                domain.getTripId(),
                firstName,
                lastName,
                telegramAlias,
                vkAlias,
                domain.getPassengerId(),
                domain.getStatus(),
                pickupLocation
        );
    }
}