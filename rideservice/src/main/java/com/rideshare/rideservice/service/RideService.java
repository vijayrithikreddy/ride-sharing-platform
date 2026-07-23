package com.rideshare.rideservice.service;

import com.rideshare.rideservice.dto.CreateRideRequestDto;
import com.rideshare.rideservice.dto.RideResponseDto;
import com.rideshare.rideservice.dto.UpdateRideRequestDto;

import java.util.List;
import java.util.UUID;

public interface RideService {
    RideResponseDto publishRide(CreateRideRequestDto rideRequestDto, UUID authUserId);

    RideResponseDto updateRide(UpdateRideRequestDto request, UUID authUserId);

    void cancelRide(UUID authUserId);

    RideResponseDto getMyActiveRide(
            UUID authUserId
    );

    List<RideResponseDto> getRideHistory(
            UUID authUserId
    );

    RideResponseDto startRide(UUID authUserId);

    RideResponseDto completeRide(UUID authUserId);

}
