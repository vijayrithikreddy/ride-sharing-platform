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
public class RideSearchResponseDto {

    private Integer rideId;

    private Double matchPercentage;

    private LocationDto source;

    private LocationDto destination;

    private LocalDateTime departureTime;

    private Double price;
    private UserSummaryDto driverProfile;


}