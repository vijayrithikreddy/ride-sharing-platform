package com.rideshare.rideservice.service;

import com.rideshare.rideservice.dto.*;
import com.rideshare.rideservice.entity.Ride;
import com.rideshare.rideservice.entity.RideRequest;
import com.rideshare.rideservice.enums.RideRequestStatus;
import com.rideshare.rideservice.enums.RideStatus;
import com.rideshare.rideservice.exception.InvalidRideException;
import com.rideshare.rideservice.exception.RideAlreadyExistsException;
import com.rideshare.rideservice.exception.RideNotFoundException;
import com.rideshare.rideservice.exception.RideRequestNotFoundException;
import com.rideshare.rideservice.feign.UserServiceClient;
import com.rideshare.rideservice.repository.RideRepository;
import com.rideshare.rideservice.repository.RideRequestRepository;
import com.rideshare.rideservice.websocket.RideEventPublisher;
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
    private final RideRequestRepository rideRequestRepository;
    private final RideEventPublisher rideEventPublisher;
    private final UserServiceClient userServiceClient;

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
        RideRequest acceptedRequest =
                rideRequestRepository
                        .findByRideIdAndStatus(
                                updatedRide.getRideId(),
                                RideRequestStatus.ACCEPTED
                        )
                        .stream()
                        .findFirst()
                        .orElseThrow(() ->
                                new RideRequestNotFoundException(
                                        "Accepted ride request not found."));
        rideEventPublisher.publishRideStarted(
                updatedRide.getDriverAuthUserId(),
                acceptedRequest.getPassengerAuthUserId(),
                updatedRide.getRideId()
        );

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
    @Override
    public boolean hasActiveRide(UUID authUserId){
        return rideRepository.existsByDriverAuthUserIdAndStatusIn(authUserId,List.of(RideStatus.AVAILABLE,RideStatus.BOOKED));
    }

    @Override
    public LiveRideResponseDto getLiveRide(Integer rideId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() ->
                        new RideNotFoundException("Ride not found."));

        RideRequest acceptedRequest =
                rideRequestRepository
                        .findByRideIdAndStatus(
                                rideId,
                                RideRequestStatus.ACCEPTED
                        )
                        .stream()
                        .findFirst()
                        .orElseThrow(() ->
                                new RideRequestNotFoundException(
                                        "Accepted ride request not found."
                                ));

        UserSummaryDto driver =
                userServiceClient
                        .getUserSummaries(
                                List.of(ride.getDriverAuthUserId()))
                        .get(0);
        System.out.println(driver.getPhoneNumber() + " phone number  ---------------------------------------------------------------------------");

        PassengerProfileDto passenger =
                userServiceClient.getPassengerProfile(
                        acceptedRequest.getPassengerAuthUserId());

        return LiveRideResponseDto.builder()

                // Ride
                .rideId(ride.getRideId())
                .rideStatus(ride.getStatus())

                .source(ride.getSource())
                .destination(ride.getDestination())

                .riderEncodedPolyline(
                        ride.getEncodedPolyline())

                .passengerEncodedPolyline(
                        acceptedRequest.getPassengerEncodedPolyline())

                // Driver
                .driverAuthUserId(
                        ride.getDriverAuthUserId())

                .driverName(
                        driver.getFirstName() + " " +
                                driver.getLastName())

                .driverPhoneNumber(
                        driver.getPhoneNumber())

                .driverProfilePicture(
                        driver.getProfilePictureUrl())

                // Passenger
                .passengerAuthUserId(
                        acceptedRequest.getPassengerAuthUserId())

                .passengerName(
                        passenger.getFirstName() + " " +
                                passenger.getLastName())

                .passengerPhoneNumber(
                        passenger.getPhoneNumber())

                .passengerProfilePicture(
                        passenger.getProfilePictureUrl())

                .build();
    }
}
