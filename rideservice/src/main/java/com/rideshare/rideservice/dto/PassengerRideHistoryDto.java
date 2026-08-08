package com.rideshare.rideservice.dto;

import com.rideshare.rideservice.enums.RideStatus;
import com.rideshare.rideservice.model.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerRideHistoryDto {

    private Integer requestId;
    private Integer rideId;

    // Rider
    private String driverName;
    private String driverPhoneNumber;
    private String driverProfilePicture;

    // Vehicle
    private String vehicleNumber;
    private String vehicleModel;
    private String vehicleColor;

    // Route
    private Location source;
    private Location destination;

    // Ride
    private Double ridePrice;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private RideStatus rideStatus;
}