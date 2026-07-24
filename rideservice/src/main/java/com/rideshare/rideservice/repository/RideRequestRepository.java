package com.rideshare.rideservice.repository;


import com.rideshare.rideservice.dto.RideRequestResponseDto;
import com.rideshare.rideservice.entity.RideRequest;
import com.rideshare.rideservice.enums.RideRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RideRequestRepository extends JpaRepository<RideRequest,Integer> {
    boolean existsByRideIdAndPassengerAuthUserId(Integer rideId,UUID passengerAuthUserId);
    List<RideRequest> findByRideIdAndStatus(Integer rideId, RideRequestStatus status);
    List<RideRequest> findByPassengerAuthUserId(UUID passengerAuthUserId);
}
