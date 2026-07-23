package com.rideshare.rideservice.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class Location {

    private Double latitude;

    private Double longitude;

    private String address;
}