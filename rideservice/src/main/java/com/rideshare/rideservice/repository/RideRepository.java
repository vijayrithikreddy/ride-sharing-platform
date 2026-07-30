package com.rideshare.rideservice.repository;

import com.rideshare.rideservice.entity.Ride;
import com.rideshare.rideservice.enums.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RideRepository extends JpaRepository<Ride,Integer> {
    Optional<Ride> findByDriverAuthUserIdAndStatus(
            UUID authUserId,
            RideStatus statuses);
    Optional<Ride> findByDriverAuthUserIdAndStatusIn(
            UUID authUserId,
            List<RideStatus> statuses);

    List<Ride> findAllByDriverAuthUserIdAndStatusIn(
            UUID authUserId,
            List<RideStatus> statuses);

    boolean existsByDriverAuthUserIdAndStatusIn(
            UUID authUserId,
            List<RideStatus> statuses);

    List<Ride> findByStatusAndDepartureTimeBetween(RideStatus status, LocalDateTime start, LocalDateTime end);
}
