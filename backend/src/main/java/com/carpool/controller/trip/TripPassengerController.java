package com.carpool.controller.trip;

import com.carpool.controller.dto.trip.TripPassengerResponse;
import com.carpool.domain.model.trip.TripPassenger;
import com.carpool.domain.service.TripPassengerService;
import com.carpool.infrastructure.security.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips/{tripId}")
public class TripPassengerController {

    private final TripPassengerService tripPassengerService;
    private final TripPassengerWebMapper webMapper;

    public TripPassengerController(TripPassengerService tripPassengerService, TripPassengerWebMapper webMapper) {
        this.tripPassengerService = tripPassengerService;
        this.webMapper = webMapper;
    }

    @PostMapping("/join")
    public ResponseEntity<TripPassengerResponse> joinTrip(
            @PathVariable Long tripId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long passengerId = userDetails.getUser().getId();
        TripPassenger request = tripPassengerService.requestToJoin(tripId, passengerId);

        return ResponseEntity.status(HttpStatus.CREATED).body(webMapper.toDto(request));
    }

    @PostMapping("/passengers/{passengerId}/approve")
    public ResponseEntity<TripPassengerResponse> approvePassenger(
            @PathVariable Long tripId,
            @PathVariable Long passengerId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long driverId = userDetails.getUser().getId();
        TripPassenger approvedRequest = tripPassengerService.approvePassenger(tripId, passengerId, driverId);

        return ResponseEntity.ok(webMapper.toDto(approvedRequest));
    }

    @DeleteMapping("/passengers/{passengerId}/reject")
    public ResponseEntity<Void> rejectPassenger(
            @PathVariable Long tripId,
            @PathVariable Long passengerId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long driverId = userDetails.getUser().getId();
        tripPassengerService.rejectPassenger(tripId, passengerId, driverId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{tripId}/passengers/my-request")
    public ResponseEntity<Void> cancelMyRequest(
            @PathVariable Long tripId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long passengerId = userDetails.getUser().getId();
        tripPassengerService.cancelPassengerRequest(tripId, passengerId);

        return ResponseEntity.noContent().build();
    }
}