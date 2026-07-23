package com.rideshare.userservice.repository;

import com.rideshare.userservice.entity.UserProfile;
import com.rideshare.userservice.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    Optional<Vehicle> findByUserProfile(UserProfile userProfile);

    boolean existsByVehicleNumber(String vehicleNumber);

}