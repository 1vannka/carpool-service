package com.carpool.controller.trip;

import com.carpool.controller.dto.trip.TripCreateRequest;
import com.carpool.controller.dto.trip.TripResponse;
import com.carpool.controller.dto.trip.TripUpdateRequest;
import com.carpool.domain.model.trip.Trip;
import com.carpool.domain.service.TripService;
import com.carpool.infrastructure.security.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;
    private final TripWebMapper tripWebMapper;

    public TripController(TripService tripService, TripWebMapper tripWebMapper) {
        this.tripService = tripService;
        this.tripWebMapper = tripWebMapper;
    }

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(
            @RequestBody TripCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Trip tripToCreate = tripWebMapper.toDomain(request);
        tripToCreate.setDriverId(userDetails.getUser().getId());

        Trip createdTrip = tripService.createTrip(tripToCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(tripWebMapper.toDto(createdTrip));
    }

    @GetMapping("/my-active")
    public ResponseEntity<TripResponse> getActiveTrip(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return tripService.getActiveTrip(userDetails.getUser().getId())
                .map(tripWebMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripResponse> updateTrip(
            @PathVariable Long id,
            @RequestBody TripUpdateRequest request) {

        Trip updatedData = tripWebMapper.toDomain(request);
        Trip savedTrip = tripService.updateTrip(id, updatedData);
        return ResponseEntity.ok(tripWebMapper.toDto(savedTrip));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TripResponse> cancelTrip(@PathVariable Long id) {
        Trip canceledTrip = tripService.cancelTrip(id);
        return ResponseEntity.ok(tripWebMapper.toDto(canceledTrip));
    }

    @GetMapping("/matching")
    public ResponseEntity<List<TripResponse>> getMatchingTrips(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long passengerId = userDetails.getUser().getId();
        List<Trip> matchingTrips = tripService.findMatchingTripsForPassenger(passengerId);

        List<TripResponse> response = matchingTrips.stream()
                .map(tripWebMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}