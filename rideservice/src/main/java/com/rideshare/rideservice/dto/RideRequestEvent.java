package com.rideshare.rideservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestEvent {

    private String type;

    private RideRequestResponseDto rideRequest;

}