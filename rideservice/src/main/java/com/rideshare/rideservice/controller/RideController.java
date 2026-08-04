package com.rideshare.rideservice.controller;


import com.rideshare.rideservice.dto.*;
import com.rideshare.rideservice.service.RideService;
import com.rideshare.rideservice.service.RouteMatchingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rides")
public class RideController {
    private final RideService rideService;
    private final RouteMatchingService routeMatchingService;

    @PostMapping
    ResponseEntity<RideResponseDto> publishRide(@RequestHeader("X-User-Id") UUID authUserId, @RequestBody CreateRideDto rideRequestDto){
        return ResponseEntity.ok(rideService.publishRide(rideRequestDto,authUserId));
    }

    @PutMapping
    public ResponseEntity<RideResponseDto> updateRide(
            @Valid @RequestBody UpdateRideDto request,
            @RequestHeader("X-User-Id") UUID authUserId) {

        return ResponseEntity.ok(
                rideService.updateRide(request, authUserId)
        );
    }

    @DeleteMapping
    public ResponseEntity<Void> cancelRide(
            @RequestHeader("X-User-Id") UUID authUserId) {

        rideService.cancelRide(authUserId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    public ResponseEntity<RideResponseDto> getMyActiveRide(
            @RequestHeader("X-User-Id") UUID authUserId) {

        return ResponseEntity.ok(rideService.getMyActiveRide(authUserId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<RideResponseDto>> getRideHistory(
            @RequestHeader("X-User-Id") UUID authUserId) {

        return ResponseEntity.ok(rideService.getRideHistory(authUserId));
    }
    @PatchMapping("/start")
    public ResponseEntity<RideResponseDto> startRide(
            @RequestHeader("X-User-Id") UUID authUserId) {

        return ResponseEntity.ok(rideService.startRide(authUserId));
    }
    @PatchMapping("/complete")
    public ResponseEntity<RideResponseDto> completeRide(
            @RequestHeader("X-User-Id") UUID authUserId) {

        return ResponseEntity.ok(rideService.completeRide(authUserId));
    }
    @PostMapping("/search")
    ResponseEntity<List<RideSearchResponseDto>> searchRides(@RequestBody SearchRideRequestDto searchRideRequestDto){
        return ResponseEntity.ok(routeMatchingService.findMatchingRides(searchRideRequestDto));

    }
}
