package com.rideshare.rideservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRideRequestDto {

    private Integer rideId;

    private LocationDto source;

    private LocationDto destination;

    private String passengerEncodedPolyline;

    private Double matchPercentage;

    private LocalDateTime departureTime;
}