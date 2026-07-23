package com.rideshare.userservice.service;

import com.rideshare.userservice.dto.CreateVehicleRequestDto;
import com.rideshare.userservice.dto.UpdateVehicleRequestDto;
import com.rideshare.userservice.dto.VehicleResponseDto;
import com.rideshare.userservice.entity.UserProfile;
import com.rideshare.userservice.entity.Vehicle;
import com.rideshare.userservice.exception.UserProfileNotFoundException;
import com.rideshare.userservice.exception.VehicleAlreadyExistsException;
import com.rideshare.userservice.exception.VehicleNotFoundException;
import com.rideshare.userservice.repository.UserProfileRepository;
import com.rideshare.userservice.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserProfileRepository userProfileRepository;
    private final ModelMapper modelMapper;

    @Override
    public VehicleResponseDto addVehicle(CreateVehicleRequestDto request,
                                         UUID authUserId) {

        UserProfile userProfile = userProfileRepository
                .findByAuthUserId(authUserId)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User profile not found."));

        if (userProfile.getVehicle() != null) {
            throw new VehicleAlreadyExistsException("Vehicle already exists.");
        }

        if (vehicleRepository.existsByVehicleNumber(request.getVehicleNumber())) {
            throw new VehicleAlreadyExistsException("Vehicle number already registered.");
        }

        Vehicle vehicle = modelMapper.map(request, Vehicle.class);

        vehicle.setUserProfile(userProfile);
        userProfile.setVehicle(vehicle);

        userProfileRepository.save(userProfile);

        return modelMapper.map(vehicle, VehicleResponseDto.class);
    }

    @Override
    public VehicleResponseDto getVehicleById(UUID authUserId) {

        UserProfile userProfile = userProfileRepository
                .findByAuthUserId(authUserId)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User profile not found."));

        Vehicle vehicle = userProfile.getVehicle();

        if (vehicle == null) {
            throw new VehicleNotFoundException("Vehicle not found.");
        }

        return modelMapper.map(vehicle, VehicleResponseDto.class);
    }

    @Override
    public VehicleResponseDto updateVehicle(UpdateVehicleRequestDto request,
                                            UUID authUserId) {

        UserProfile userProfile = userProfileRepository
                .findByAuthUserId(authUserId)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User profile not found."));

        Vehicle vehicle = userProfile.getVehicle();

        if (vehicle == null) {
            throw new VehicleNotFoundException("Vehicle not found.");
        }

        modelMapper.map(request, vehicle);

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);

        return modelMapper.map(updatedVehicle, VehicleResponseDto.class);
    }

    @Override
    public void deleteVehicle(UUID authUserId) {

        UserProfile userProfile = userProfileRepository
                .findByAuthUserId(authUserId)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User profile not found."));

        Vehicle vehicle = userProfile.getVehicle();

        if (vehicle == null) {
            throw new VehicleNotFoundException("Vehicle not found.");
        }

        userProfile.setVehicle(null);

        vehicleRepository.delete(vehicle);
    }
}