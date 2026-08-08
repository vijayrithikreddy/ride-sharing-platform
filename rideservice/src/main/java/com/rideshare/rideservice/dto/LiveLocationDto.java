package com.rideshare.rideservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveLocationDto {

    private Integer rideId;

    private double latitude;

    private double longitude;
}