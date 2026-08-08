package com.rideshare.rideservice.dto;

import com.rideshare.rideservice.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideHistoryDto {

    private Integer rideId;

    // Passenger
    private String passengerName;
    private String passengerPhoneNumber;
    private String passengerProfilePicture;

    // Route
    private String source;
    private String destination;

    // Ride
    private Double ridePrice;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private RideStatus status;
}