package com.rideshare.rideservice.dto;

import com.rideshare.rideservice.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleSummaryDto {

    private VehicleType vehicleType;
    private String vehicleNumber;
    private String brand;

    private String model;

    private String color;
}