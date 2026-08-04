package com.rideshare.rideservice.dto;

import com.rideshare.rideservice.enums.RideRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideRequestResponseDto {

    private Integer requestId;

    private RideRequestStatus status;

    private LocalDateTime requestedAt;

    private PassengerProfileDto passengerProfile;

    private LocationDto source;

    private LocationDto destination;

    private LocalDateTime departureTime;

    private Double ridePrice;
}