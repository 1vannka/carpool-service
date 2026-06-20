package com.carpool.controller.trip;

import com.carpool.controller.dto.trip.TripPassengerResponse;
import com.carpool.domain.model.trip.TripPassenger;
import com.carpool.domain.service.TripPassengerService;
import com.carpool.domain.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips/{tripId}")
public class TripPassengerController {

    private final TripPassengerService tripPassengerService;
    private final TripPassengerWebMapper webMapper;
    private final UserService userService;

    public TripPassengerController(TripPassengerService tripPassengerService, TripPassengerWebMapper webMapper, UserService userService) {
        this.tripPassengerService = tripPassengerService;
        this.webMapper = webMapper;
        this.userService = userService;
    }

    @PostMapping("/join")
    public ResponseEntity<TripPassengerResponse> joinTrip(
            @PathVariable Long tripId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long passengerId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();
        TripPassenger request = tripPassengerService.requestToJoin(tripId, passengerId);

        return ResponseEntity.status(HttpStatus.CREATED).body(webMapper.toDto(request));
    }

    @PostMapping("/passengers/{passengerId}/approve")
    public ResponseEntity<TripPassengerResponse> approvePassenger(
            @PathVariable Long tripId,
            @PathVariable Long passengerId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long driverId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();
        TripPassenger approvedRequest = tripPassengerService.approvePassenger(tripId, passengerId, driverId);

        return ResponseEntity.ok(webMapper.toDto(approvedRequest));
    }

    @DeleteMapping("/passengers/{passengerId}/reject")
    public ResponseEntity<Void> rejectPassenger(
            @PathVariable Long tripId,
            @PathVariable Long passengerId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long driverId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();
        tripPassengerService.rejectPassenger(tripId, passengerId, driverId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/passengers/my-request")
    public ResponseEntity<Void> cancelMyRequest(
            @PathVariable Long tripId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long passengerId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();
        tripPassengerService.cancelPassengerRequest(tripId, passengerId);

        return ResponseEntity.noContent().build();
    }
}