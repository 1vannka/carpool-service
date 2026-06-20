package com.carpool.controller.trip;

import com.carpool.controller.dto.trip.TripCreateRequest;
import com.carpool.controller.dto.trip.TripResponse;
import com.carpool.controller.dto.trip.TripUpdateRequest;
import com.carpool.domain.model.trip.Trip;
import com.carpool.domain.service.TripService;
import com.carpool.domain.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;
    private final TripWebMapper tripWebMapper;
    private final UserService userService;

    public TripController(TripService tripService, TripWebMapper tripWebMapper, UserService userService) {
        this.tripService = tripService;
        this.tripWebMapper = tripWebMapper;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(
            @Valid @RequestBody TripCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();

        Trip tripToCreate = tripWebMapper.toDomain(request);
        tripToCreate.setDriverId(userId);

        Trip createdTrip = tripService.createTrip(tripToCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(tripWebMapper.toDto(createdTrip));
    }

    @GetMapping("/my-active")
    public ResponseEntity<TripResponse> getActiveTrip(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();

        return tripService.getActiveTrip(userId)
                .map(tripWebMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripResponse> updateTrip(
            @PathVariable Long id,
            @Valid @RequestBody TripUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();
        Trip updatedData = tripWebMapper.toDomain(request);

        Trip savedTrip = tripService.updateTrip(id, userId, updatedData);

        return ResponseEntity.ok(tripWebMapper.toDto(savedTrip));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TripResponse> cancelTrip(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();
        Trip canceledTrip = tripService.cancelTrip(id, userId);

        return ResponseEntity.ok(tripWebMapper.toDto(canceledTrip));
    }

    @GetMapping("/matching")
    public ResponseEntity<List<TripResponse>> getMatchingTrips(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long passengerId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();
        List<Trip> matchingTrips = tripService.findMatchingTripsForPassenger(passengerId);

        List<TripResponse> response = matchingTrips.stream()
                .map(tripWebMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TripResponse>> getAvailableTrips(
            @RequestParam Long officeId) {

        List<TripResponse> response = tripService.getAvailableTripsForOffice(officeId).stream()
                .map(tripWebMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}