package com.rideshare.userservice.service;

import com.rideshare.userservice.dto.CreateVehicleRequestDto;
import com.rideshare.userservice.dto.UpdateVehicleRequestDto;
import com.rideshare.userservice.dto.VehicleResponseDto;

import java.util.UUID;

public interface VehicleService {

    VehicleResponseDto addVehicle(
            CreateVehicleRequestDto request,
            UUID authUserId
    );

    VehicleResponseDto getVehicleById(
            UUID authUserId
    );

    VehicleResponseDto updateVehicle(
            UpdateVehicleRequestDto request,
            UUID authUserId
    );

    void deleteVehicle(
            UUID authUserId
    );

}