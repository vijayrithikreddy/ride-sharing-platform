package com.rideshare.rideservice.service;

import com.rideshare.rideservice.dto.CreateRideDto;
import com.rideshare.rideservice.dto.RideResponseDto;
import com.rideshare.rideservice.dto.UpdateRideDto;
import com.rideshare.rideservice.entity.Ride;
import com.rideshare.rideservice.enums.RideStatus;
import com.rideshare.rideservice.exception.InvalidRideException;
import com.rideshare.rideservice.exception.RideAlreadyExistsException;
import com.rideshare.rideservice.exception.RideNotFoundException;
import com.rideshare.rideservice.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService{
    private final RideRepository rideRepository;
    private final ModelMapper modelMapper;

    @Override
    public RideResponseDto publishRide(CreateRideDto rideRequestDto, UUID authUserId) {
        if (rideRepository.existsByDriverAuthUserIdAndStatusIn(
                authUserId,
                List.of(
                        RideStatus.AVAILABLE,
                        RideStatus.BOOKED,
                        RideStatus.STARTED
                ))) {

            throw new RideAlreadyExistsException(
                    "You already have an active ride."
            );
        }
        if (rideRequestDto.getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new InvalidRideException(
                    "Departure time must be in the future."
            );
        }


        Ride currentRide = modelMapper.map(rideRequestDto, Ride.class);

        currentRide.setDriverAuthUserId(authUserId);
        Ride savedRide = rideRepository.save(currentRide);

        return modelMapper.map(savedRide, RideResponseDto.class);
    }

    @Override
    public RideResponseDto updateRide(UpdateRideDto updateRideDto, UUID authUserId) {

        Ride ride = rideRepository.findByDriverAuthUserIdAndStatus(authUserId,RideStatus.AVAILABLE)
                .orElseThrow(() ->
                        new RideNotFoundException("Ride not found."));


        modelMapper.map(updateRideDto, ride);

        Ride updatedRide = rideRepository.save(ride);

        return modelMapper.map(updatedRide, RideResponseDto.class);
    }
    @Override
    public void cancelRide(UUID authUserId) {

        Ride ride = rideRepository.findByDriverAuthUserIdAndStatusIn(
                authUserId,
                List.of(
                        RideStatus.AVAILABLE,
                        RideStatus.BOOKED,
                        RideStatus.STARTED
                )
        ).orElseThrow(() ->
                new RideNotFoundException("No active ride found."));

        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancelledAt(LocalDateTime.now());

        rideRepository.save(ride);
    }

    @Override
    public RideResponseDto getMyActiveRide(UUID authUserId) {

        Ride ride = rideRepository
                .findByDriverAuthUserIdAndStatusIn(
                        authUserId,
                        List.of(
                                RideStatus.AVAILABLE,
                                RideStatus.BOOKED,
                                RideStatus.STARTED
                        ))
                .orElseThrow(() ->
                        new RideNotFoundException("No active ride found."));

        return modelMapper.map(ride, RideResponseDto.class);
    }

    @Override
    public List<RideResponseDto> getRideHistory(UUID authUserId) {

        return rideRepository
                .findByDriverAuthUserIdAndStatusIn(
                        authUserId,
                        List.of(
                                RideStatus.COMPLETED,
                                RideStatus.CANCELLED
                        ))
                .stream()
                .map(ride ->
                        modelMapper.map(ride, RideResponseDto.class))
                .toList();
    }

    @Override
    public RideResponseDto startRide(UUID authUserId) {

        Ride ride = rideRepository
                .findByDriverAuthUserIdAndStatus(authUserId, RideStatus.BOOKED)
                .orElseThrow(() ->
                        new RideNotFoundException("No booked ride found."));

        ride.setStatus(RideStatus.STARTED);
        ride.setStartedAt(LocalDateTime.now());

        Ride updatedRide = rideRepository.save(ride);

        return modelMapper.map(updatedRide,RideResponseDto.class);
    }

    @Override
    public RideResponseDto completeRide(UUID authUserId) {

        Ride ride = rideRepository
                .findByDriverAuthUserIdAndStatus(authUserId, RideStatus.STARTED)
                .orElseThrow(() ->
                        new RideNotFoundException("No started ride found."));

        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());

        Ride updatedRide = rideRepository.save(ride);

        return modelMapper.map(updatedRide, RideResponseDto.class);
    }
}
