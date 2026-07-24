package com.rideshare.rideservice.controller;


import com.rideshare.rideservice.dto.CreateRideDto;
import com.rideshare.rideservice.dto.RideResponseDto;
import com.rideshare.rideservice.dto.UpdateRideDto;
import com.rideshare.rideservice.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/rides")
public class RideController {
    private final RideService rideService;

    @PostMapping
    ResponseEntity<RideResponseDto> publishRide(@RequestParam UUID authUserId, @RequestBody CreateRideDto rideRequestDto){
        return ResponseEntity.ok(rideService.publishRide(rideRequestDto,authUserId));
    }

    @PutMapping
    public ResponseEntity<RideResponseDto> updateRide(
            @Valid @RequestBody UpdateRideDto request,
            @RequestParam UUID authUserId) {

        return ResponseEntity.ok(
                rideService.updateRide(request, authUserId)
        );
    }

    @DeleteMapping
    public ResponseEntity<Void> cancelRide(
            @RequestParam UUID authUserId) {

        rideService.cancelRide(authUserId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    public ResponseEntity<RideResponseDto> getMyActiveRide(
            @RequestParam UUID authUserId) {

        return ResponseEntity.ok(rideService.getMyActiveRide(authUserId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<RideResponseDto>> getRideHistory(
            @RequestParam UUID authUserId) {

        return ResponseEntity.ok(rideService.getRideHistory(authUserId));
    }
    @PatchMapping("/start")
    public ResponseEntity<RideResponseDto> startRide(
            @RequestParam UUID authUserId) {

        return ResponseEntity.ok(rideService.startRide(authUserId));
    }
    @PatchMapping("/complete")
    public ResponseEntity<RideResponseDto> completeRide(
            @RequestParam UUID authUserId) {

        return ResponseEntity.ok(rideService.completeRide(authUserId));
    }
}
