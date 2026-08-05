package com.rideshare.rideservice.dto;

import com.rideshare.rideservice.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideResponseDto {

    private Integer rideId;

    private LocationDto source;

    private LocationDto destination;

    private LocalDateTime departureTime;
    private String encodedPolyline;

    private Double price;

    private RideStatus status;
}