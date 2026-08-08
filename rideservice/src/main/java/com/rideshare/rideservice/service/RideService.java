package com.rideshare.rideservice.service;

import com.rideshare.rideservice.dto.*;

import java.util.List;
import java.util.UUID;

public interface RideService {
    RideResponseDto publishRide(CreateRideDto rideRequestDto, UUID authUserId);

    RideResponseDto updateRide(UpdateRideDto request, UUID authUserId);

    void cancelRide(UUID authUserId);

    RideResponseDto getMyActiveRide(
            UUID authUserId
    );

    List<RideHistoryDto> getRideHistory(
            UUID authUserId
    );

    RideResponseDto startRide(UUID authUserId);

    RideResponseDto completeRide(UUID authUserId);
    boolean hasActiveRide(UUID authUserId);
    LiveRideResponseDto getLiveRide(Integer rideId);
    void updateDriverLocation(LiveLocationDto dto, UUID driverAuthUserId);
    void updatePassengerLocation(LiveLocationDto dto, UUID passengerAuthUserId);

}
