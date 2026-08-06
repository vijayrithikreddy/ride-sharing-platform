package com.rideshare.rideservice.dto;

import com.rideshare.rideservice.enums.RideRequestStatus;
import com.rideshare.rideservice.enums.RideStatus;
import com.rideshare.rideservice.model.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestRideResponseDto {

    // =========================
    // Request Details
    // =========================

    private Integer requestId;
    private RideRequestStatus status;
    private LocalDateTime requestedAt;

    // =========================
    // Ride Details
    // =========================

    private Integer rideId;
    private RideStatus rideStatus;

    private UUID driverAuthUserId;

    private Location source;
    private Location destination;

    private String riderEncodedPolyline;
    private String passengerEncodedPolyline;

    private LocalDateTime departureTime;
    private Double matchPercentage;

    private Double ridePrice;

    private Integer availableSeats;

    // =========================
    // Rider Details (User Service)
    // =========================

    private String driverName;
    private String driverPhoneNumber;
    private String driverProfilePicture;

    private String vehicleNumber;
    private String vehicleModel;
    private String vehicleColor;
}