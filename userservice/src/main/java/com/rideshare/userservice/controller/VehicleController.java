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

    @PostMapping("/{authUserId}")
    public ResponseEntity<VehicleResponseDto> addVehicle(
            @PathVariable UUID authUserId,
            @Valid @RequestBody CreateVehicleRequestDto request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehicleService.addVehicle(request, authUserId));
    }

    @GetMapping("/{authUserId}")
    public ResponseEntity<VehicleResponseDto> getMyVehicle(
            @PathVariable UUID authUserId) {

        return ResponseEntity.ok(
                vehicleService.getVehicleById(authUserId)
        );
    }

    @PutMapping("/{authUserId}")
    public ResponseEntity<VehicleResponseDto> updateVehicle(
            @PathVariable UUID authUserId,
            @Valid @RequestBody UpdateVehicleRequestDto request) {

        return ResponseEntity.ok(
                vehicleService.updateVehicle(request, authUserId)
        );
    }

    @DeleteMapping("/{authUserId}")
    public ResponseEntity<Void> deleteVehicle(
            @PathVariable UUID authUserId) {

        vehicleService.deleteVehicle(authUserId);

        return ResponseEntity.noContent().build();
    }
}