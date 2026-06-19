package com.carpool.controller.ride;

import com.carpool.controller.dto.ride.RideRequestCreateRequest;
import com.carpool.controller.dto.ride.RideRequestResponse;
import com.carpool.domain.model.ride.RideRequest;
import com.carpool.domain.service.RideRequestService;
import com.carpool.infrastructure.security.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ride-requests")
public class RideRequestController {

    private final RideRequestService rideRequestService;
    private final RideRequestWebMapper webMapper;

    public RideRequestController(RideRequestService rideRequestService, RideRequestWebMapper webMapper) {
        this.rideRequestService = rideRequestService;
        this.webMapper = webMapper;
    }

    @PostMapping
    public ResponseEntity<RideRequestResponse> createRideRequest(
            @RequestBody RideRequestCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        RideRequest domainRequest = webMapper.toDomain(request);
        domainRequest.setPassengerId(userDetails.getUser().getId());

        RideRequest createdRequest = rideRequestService.createRideRequest(domainRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(webMapper.toDto(createdRequest));
    }

    @GetMapping("/my-active")
    public ResponseEntity<RideRequestResponse> getMyActiveRequest(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return rideRequestService.getActiveRequest(userDetails.getUser().getId())
                .map(webMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelRideRequest(@PathVariable Long id) {
        rideRequestService.cancelRideRequest(id);
        return ResponseEntity.noContent().build();
    }
}