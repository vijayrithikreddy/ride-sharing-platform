package com.rideshare.rideservice.model;

import com.rideshare.rideservice.entity.Ride;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class RideMatch {

    private Ride ride;

    private double matchPercentage;
}