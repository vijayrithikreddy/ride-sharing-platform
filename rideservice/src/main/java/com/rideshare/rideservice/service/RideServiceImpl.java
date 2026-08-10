package com.rideshare.rideservice.service;

import com.rideshare.rideservice.dto.*;
import com.rideshare.rideservice.entity.Ride;
import com.rideshare.rideservice.entity.RideRequest;
import com.rideshare.rideservice.enums.RideRequestStatus;
import com.rideshare.rideservice.enums.RideStatus;
import com.rideshare.rideservice.event.RideNotificationPublisher;
import com.rideshare.rideservice.exception.InvalidRideException;
import com.rideshare.rideservice.exception.RideAlreadyExistsException;
import com.rideshare.rideservice.exception.RideNotFoundException;
import com.rideshare.rideservice.exception.RideRequestNotFoundException;
import com.rideshare.rideservice.feign.UserServiceClient;
import com.rideshare.rideservice.repository.RideRepository;
import com.rideshare.rideservice.repository.RideRequestRepository;
import com.rideshare.rideservice.websocket.RideEventPublisher;
import com.rideshare.rideservice.websocket.RideLiveLocation;
import com.rideshare.rideservice.websocket.RideLocationStore;
import jakarta.transaction.Transactional;
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
    private final RideNotificationPublisher rideNotificationPublisher;
    private final UserServiceClient userServiceClient;
    private final RideLocationStore rideLocationStore;


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
                        )
                )
                .orElseThrow(() ->
                        new RideNotFoundException("No active ride found."));

        RideResponseDto response =
                modelMapper.map(ride, RideResponseDto.class);

        if (ride.getStatus() != RideStatus.AVAILABLE) {

            RideRequest acceptedRequest =
                    rideRequestRepository
                            .findByRideIdAndStatus(
                                    ride.getRideId(),
                                    RideRequestStatus.ACCEPTED
                            )
                            .orElseThrow(() ->
                                    new RideRequestNotFoundException(
                                            "Accepted ride request not found."
                                    ));

            PassengerProfileDto passenger =
                    userServiceClient.getPassengerProfile(
                            acceptedRequest.getPassengerAuthUserId()
                    );

            response.setPassengerProfile(passenger);

        }

        return response;
    }

    @Override
    public List<RideHistoryDto> getRideHistory(UUID authUserId) {

        List<Ride> rides = rideRepository
                .findAllByDriverAuthUserIdAndStatusIn(
                        authUserId,
                        List.of(RideStatus.COMPLETED)
                );

        return rides.stream()
                .map(ride -> {

                    RideRequest acceptedRequest =
                            rideRequestRepository
                                    .findByRideIdAndStatus(
                                            ride.getRideId(),
                                            RideRequestStatus.COMPLETED
                                    )
                                    .orElseThrow(() ->
                                            new RideRequestNotFoundException(
                                                    "Completed ride request not found."
                                            ));

                    PassengerProfileDto passenger =
                            userServiceClient.getPassengerProfile(
                                    acceptedRequest.getPassengerAuthUserId()
                            );

                    return RideHistoryDto.builder()

                            .rideId(
                                    ride.getRideId()
                            )

                            .passengerName(
                                    passenger.getFirstName() + " " +
                                            passenger.getLastName()
                            )

                            .passengerPhoneNumber(
                                    passenger.getPhoneNumber()
                            )

                            .passengerProfilePicture(
                                    passenger.getProfilePictureUrl()
                            )

                            .source(
                                    ride.getSource().getAddress()
                            )

                            .destination(
                                    ride.getDestination().getAddress()
                            )

                            .ridePrice(
                                    ride.getRidePrice()
                            )

                            .startedAt(
                                    ride.getStartedAt()
                            )

                            .completedAt(
                                    ride.getCompletedAt()
                            )

                            .status(
                                    ride.getStatus()
                            )

                            .build();

                })
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
        UserSummaryDto driver =
                userServiceClient
                        .getUserSummaries(
                                List.of(updatedRide.getDriverAuthUserId())
                        )
                        .get(0);

        // Send email notification through RabbitMQ
        rideNotificationPublisher.publishRideStarted(
                driver.getEmail(),
                Long.valueOf(updatedRide.getRideId())
        );

        return modelMapper.map(updatedRide,RideResponseDto.class);
    }

    @Override
    @Transactional
    public RideResponseDto completeRide(UUID authUserId) {

        Ride ride = rideRepository
                .findByDriverAuthUserIdAndStatus(
                        authUserId,
                        RideStatus.STARTED
                )
                .orElseThrow(() ->
                        new RideNotFoundException(
                                "No started ride found."
                        ));

        RideRequest rideRequest = rideRequestRepository
                .findByRideIdAndStatus(
                        ride.getRideId(),
                        RideRequestStatus.ACCEPTED
                )
                .orElseThrow(() ->
                        new RideRequestNotFoundException(
                                "Accepted ride request not found."
                        ));

        // Complete ride
        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());

        // Complete ride request
        rideRequest.setStatus(RideRequestStatus.COMPLETED);

        // Persist both changes
        Ride updatedRide = rideRepository.save(ride);
        rideRequestRepository.save(rideRequest);

        // Remove live location
        rideLocationStore.remove(
                ride.getRideId()
        );

        // Notify both users
        rideEventPublisher.publishRideCompleted(
                updatedRide.getDriverAuthUserId(),
                rideRequest.getPassengerAuthUserId(),
                updatedRide.getRideId()
        );
        UserSummaryDto driver =
                userServiceClient
                        .getUserSummaries(
                                List.of(updatedRide.getDriverAuthUserId())
                        )
                        .get(0);

        // Send email notification through RabbitMQ
        rideNotificationPublisher.publishRideCompleted(
                driver.getEmail(),
                Long.valueOf(updatedRide.getRideId())
        );

        return modelMapper.map(
                updatedRide,
                RideResponseDto.class
        );
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

    @Override
    public void updateDriverLocation(
            LiveLocationDto dto,
            UUID driverAuthUserId
    ) {

        Ride ride = rideRepository
                .findByDriverAuthUserIdAndStatus(
                        driverAuthUserId,
                        RideStatus.STARTED
                )
                .orElseThrow(() ->
                        new RideNotFoundException(
                                "No active ride found."
                        ));

        dto.setRideId(ride.getRideId());

        rideLocationStore.updateDriverLocation(
                ride.getRideId(),
                dto
        );

        RideLiveLocation liveLocation =
                rideLocationStore.getLocation(
                        ride.getRideId()
                );

        RideLiveLocationDto response =
                RideLiveLocationDto.builder()
                        .driverLocation(
                                liveLocation.getDriverLocation()
                        )
                        .passengerLocation(
                                liveLocation.getPassengerLocation()
                        )
                        .build();

        rideEventPublisher.publishLiveLocation(
                ride.getRideId(),
                response
        );
    }
   @Override
    public void updatePassengerLocation(
            LiveLocationDto dto,
            UUID passengerAuthUserId
    ) {
       System.out.println("========== PASSENGER LOCATION ==========");
       System.out.println("User : " + passengerAuthUserId);
       System.out.println("Lat : " + dto.getLatitude());
       System.out.println("Lng : " + dto.getLongitude());

        RideRequest rideRequest = rideRequestRepository
                .findByPassengerAuthUserIdAndStatus(
                        passengerAuthUserId,
                        RideRequestStatus.ACCEPTED
                )
                .orElseThrow(() ->
                        new RideRequestNotFoundException(
                                "No active ride request found."
                        ));

        Ride ride = rideRepository
                .findById(rideRequest.getRideId())
                .orElseThrow(() ->
                        new RideNotFoundException(
                                "Ride not found."
                        ));

        if (ride.getStatus() != RideStatus.STARTED) {
            throw new InvalidRideException(
                    "Ride has not started yet."
            );
        }

        dto.setRideId(ride.getRideId());

        rideLocationStore.updatePassengerLocation(
                ride.getRideId(),
                dto
        );

        RideLiveLocation liveLocation =
                rideLocationStore.getLocation(
                        ride.getRideId()
                );

        RideLiveLocationDto response =
                RideLiveLocationDto.builder()
                        .driverLocation(
                                liveLocation.getDriverLocation()
                        )
                        .passengerLocation(
                                liveLocation.getPassengerLocation()
                        )
                        .build();

        rideEventPublisher.publishLiveLocation(
                ride.getRideId(),
                response
        );
    }

}
