package com.rideshare.userservice.controller;

import com.rideshare.userservice.dto.CreateVehicleRequestDto;
import com.rideshare.userservice.dto.UpdateVehicleRequestDto;
import com.rideshare.userservice.dto.VehicleResponseDto;
import com.rideshare.userservice.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping("/me")
    public ResponseEntity<VehicleResponseDto> addVehicle(
            @RequestHeader("X-User-Id") UUID authUserId,
            @Valid @RequestBody CreateVehicleRequestDto request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehicleService.addVehicle(request, authUserId));
    }

    @GetMapping("/me")
    public ResponseEntity<VehicleResponseDto> getMyVehicle(
            @RequestHeader("X-User-Id") UUID authUserId) {

        return ResponseEntity.ok(
                vehicleService.getVehicleById(authUserId)
        );
    }

    @PutMapping("/me")
    public ResponseEntity<VehicleResponseDto> updateVehicle(
            @RequestHeader("X-User-Id") UUID authUserId,
            @Valid @RequestBody UpdateVehicleRequestDto request) {

        return ResponseEntity.ok(
                vehicleService.updateVehicle(request, authUserId)
        );
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteVehicle(
            @RequestHeader("X-User-Id") UUID authUserId) {

        vehicleService.deleteVehicle(authUserId);

        return ResponseEntity.noContent().build();
    }
}