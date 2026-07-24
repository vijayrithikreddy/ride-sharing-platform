package com.rideshare.userservice.dto;

import com.rideshare.userservice.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponseDto {

    private String vehicleNumber;

    private VehicleType vehicleType;

    private String brand;

    private String model;

    private String color;
}