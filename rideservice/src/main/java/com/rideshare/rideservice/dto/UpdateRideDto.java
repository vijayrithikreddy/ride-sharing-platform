package com.rideshare.rideservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRideDto {

    @Valid
    @NotNull(message = "Source location is required")
    private LocationDto source;

    @Valid
    @NotNull(message = "Destination location is required")
    private LocationDto destination;

    @NotBlank(message = "Route polyline is required")
    private String encodedPolyline;

    @NotNull(message = "Departure time is required")
    @Future(message = "Departure time must be in the future")
    private LocalDateTime departureTime;

    @NotNull(message = "Ride price is required")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Ride price must be greater than zero")
    private Double price;
}