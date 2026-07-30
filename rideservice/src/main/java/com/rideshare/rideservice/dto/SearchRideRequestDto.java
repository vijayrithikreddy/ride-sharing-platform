package com.rideshare.rideservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SearchRideRequestDto {

    private LocationDto source;

    private LocationDto destination;

    private String passengerEncodedPolyline;

    private LocalDateTime departureTime;

}
