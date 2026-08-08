package com.rideshare.rideservice.controller;

import com.rideshare.rideservice.dto.CreateRideRequestDto;
import com.rideshare.rideservice.dto.PassengerRideHistoryDto;
import com.rideshare.rideservice.dto.RequestRideResponseDto;
import com.rideshare.rideservice.dto.RideRequestResponseDto;
import com.rideshare.rideservice.service.RideRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/ride-requests")
@RequiredArgsConstructor
public class RideRequestController {

    private final RideRequestService rideRequestService;

    @PostMapping("/request")
    public ResponseEntity<RideRequestResponseDto> requestRide(
            @RequestBody CreateRideRequestDto request,
            @RequestHeader("X-User-Id") UUID passengerAuthUserId) {

        return ResponseEntity.status(HttpStatus.CREATED).body(rideRequestService.requestRide(request, passengerAuthUserId));
    }

    @PutMapping("/accept")
    public ResponseEntity<RideRequestResponseDto> acceptRideRequest(
            @RequestParam Integer requestId,
            @RequestHeader("X-User-Id") UUID driverAuthUserId) {

        return ResponseEntity.ok(rideRequestService.acceptRideRequest(requestId, driverAuthUserId));
    }

    @PutMapping("/reject")
    public ResponseEntity<RideRequestResponseDto> rejectRideRequest(
            @RequestParam Integer requestId,
            @RequestHeader("X-User-Id") UUID driverAuthUserId) {

        return ResponseEntity.ok(rideRequestService.rejectRideRequest(requestId, driverAuthUserId));
    }

    @PutMapping("/cancel")
    public ResponseEntity<Void> cancelRideRequest(
            @RequestParam Integer requestId,
            @RequestHeader("X-User-Id") UUID passengerAuthUserId) {

        rideRequestService.cancelRideRequest(requestId, passengerAuthUserId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/myrequests")
    public ResponseEntity<List<RideRequestResponseDto>> getMyRideRequests(
            @RequestHeader("X-User-Id") UUID passengerAuthUserId) {

        return ResponseEntity.ok(rideRequestService.getMyRideRequests(passengerAuthUserId)
        );
    }

    @GetMapping("/myriderequests")
    public ResponseEntity<List<RideRequestResponseDto>> getPendingRideRequests(
            @RequestHeader("X-User-Id") UUID driverAuthUserId) {

        return ResponseEntity.ok(rideRequestService.getRideRequests(driverAuthUserId));
    }
   @GetMapping("/myactiverequests")
    public ResponseEntity<List<RequestRideResponseDto>> getMyActiveRideRequests(@RequestHeader("X-User-Id") UUID passengerAuthId){
        return ResponseEntity.ok(rideRequestService.getMyActiveRequests(passengerAuthId));
   }
    @GetMapping("/history")
    public ResponseEntity<List<PassengerRideHistoryDto>> getPassengerRideHistory(
            @RequestHeader("X-User-Id") UUID passengerAuthUserId
    ) {

        return ResponseEntity.ok(rideRequestService.getPassengerRideHistory(passengerAuthUserId));

    }

}