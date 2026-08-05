package com.rideshare.rideservice.service;

import com.rideshare.rideservice.dto.CreateRideRequestDto;
import com.rideshare.rideservice.dto.RideRequestResponseDto;

import java.util.List;
import java.util.UUID;

public interface RideRequestService {
    RideRequestResponseDto requestRide(CreateRideRequestDto request, UUID passengerAuthUserId);

    RideRequestResponseDto acceptRideRequest(Integer requestId, UUID driverAuthUserId);

    RideRequestResponseDto rejectRideRequest(Integer requestId, UUID driverAuthUserId);

    void cancelRideRequest(Integer requestId, UUID passengerAuthUserId);

    List<RideRequestResponseDto> getMyRideRequests(UUID passengerAuthUserId);

    List<RideRequestResponseDto> getRideRequests(UUID driverAuthUserId);
    List<RideRequestResponseDto> getActiveRideRequests(UUID passengerAuthUserId);
}
