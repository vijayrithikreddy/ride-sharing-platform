package com.rideshare.rideservice.service;

import com.rideshare.rideservice.dto.CreateRideDto;
import com.rideshare.rideservice.dto.LiveRideResponseDto;
import com.rideshare.rideservice.dto.RideResponseDto;
import com.rideshare.rideservice.dto.UpdateRideDto;

import java.util.List;
import java.util.UUID;

public interface RideService {
    RideResponseDto publishRide(CreateRideDto rideRequestDto, UUID authUserId);

    RideResponseDto updateRide(UpdateRideDto request, UUID authUserId);

    void cancelRide(UUID authUserId);

    RideResponseDto getMyActiveRide(
            UUID authUserId
    );

    List<RideResponseDto> getRideHistory(
            UUID authUserId
    );

    RideResponseDto startRide(UUID authUserId);

    RideResponseDto completeRide(UUID authUserId);
    boolean hasActiveRide(UUID authUserId);
    LiveRideResponseDto getLiveRide(Integer rideId);

}
