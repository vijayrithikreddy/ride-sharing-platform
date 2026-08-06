package com.rideshare.rideservice.dto;

import com.rideshare.rideservice.enums.RideStatus;
import com.rideshare.rideservice.model.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveRideResponseDto {

    // Ride
    private Integer rideId;
    private RideStatus rideStatus;

    private String riderEncodedPolyline;
    private String passengerEncodedPolyline;

    private Location source;
    private Location destination;

    // Driver
    private UUID driverAuthUserId;
    private String driverName;
    private String driverPhoneNumber;
    private String driverProfilePicture;

    // Passenger
    private UUID passengerAuthUserId;
    private String passengerName;
    private String passengerPhoneNumber;
    private String passengerProfilePicture;
}