package com.rideshare.rideservice.service;

import com.rideshare.rideservice.dto.RideSearchResponseDto;
import com.rideshare.rideservice.dto.SearchRideRequestDto;

import java.util.List;

public interface RouteMatchingService {
    List<RideSearchResponseDto> findMatchingRides(SearchRideRequestDto request);

}
