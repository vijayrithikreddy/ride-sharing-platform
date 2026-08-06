package com.rideshare.rideservice.service;

import com.rideshare.rideservice.dto.*;
import com.rideshare.rideservice.entity.Ride;
import com.rideshare.rideservice.entity.RideRequest;
import com.rideshare.rideservice.enums.RideRequestStatus;
import com.rideshare.rideservice.enums.RideStatus;
import com.rideshare.rideservice.exception.*;
import com.rideshare.rideservice.feign.UserServiceClient;
import com.rideshare.rideservice.model.Location;
import com.rideshare.rideservice.repository.RideRepository;
import com.rideshare.rideservice.repository.RideRequestRepository;
import com.rideshare.rideservice.websocket.RideEventPublisher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RideRequestServiceImpl implements RideRequestService{
    private final RideRequestRepository rideRequestRepository;
    private final ModelMapper modelMapper;
    private final RideRepository rideRepository;
    private final RideEventPublisher rideEventPublisher;
    private final UserServiceClient userServiceClient;

    @Override
    public RideRequestResponseDto requestRide(CreateRideRequestDto request, UUID passengerAuthUserId) {

        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new RideNotFoundException("Ride not found."));

        if (ride.getStatus() != RideStatus.AVAILABLE) {
            throw new RideNotAvailableException("Ride is not available.");
        }

        if (ride.getDriverAuthUserId().equals(passengerAuthUserId)) {
            throw new InvalidRideRequestException(
                    "You cannot request your own ride.");
        }

        if (rideRequestRepository.existsByRideIdAndPassengerAuthUserId(request.getRideId(), passengerAuthUserId)) {
            throw new RideRequestAlreadyExistsException("Request request already exists.");
        }

        RideRequest rideRequest = RideRequest.builder()

                .rideId(request.getRideId())

                .passengerAuthUserId(passengerAuthUserId)

                .source(
                        modelMapper.map(
                                request.getSource(),
                                Location.class))

                .destination(
                        modelMapper.map(
                                request.getDestination(),
                                Location.class))

                .passengerEncodedPolyline(
                        request.getPassengerEncodedPolyline())

                .departureTime(
                        request.getDepartureTime())
                .ridePrice(request.getRidePrice())

                .matchPercentage(
                        request.getMatchPercentage())

                .build();


        RideRequest savedRideRequest =
                rideRequestRepository.save(rideRequest);
        PassengerProfileDto passengerProfile = userServiceClient.getPassengerProfile(passengerAuthUserId);

        RideRequestResponseDto response =
                modelMapper.map(
                        savedRideRequest,
                        RideRequestResponseDto.class
                );
        response.setPassengerProfile(passengerProfile);

        rideEventPublisher.publishNewRideRequest(
                ride.getRideId(),
                response
        );

        return response;
    }


    @Override
    @Transactional
    public RideRequestResponseDto acceptRideRequest(Integer requestId, UUID driverAuthUserId) {

        RideRequest rideRequest = rideRequestRepository.findById(requestId)
                .orElseThrow(() ->
                        new RideRequestNotFoundException("Ride request not found."));

        Ride ride = rideRepository.findById(rideRequest.getRideId())
                .orElseThrow(() ->
                        new RideNotFoundException("Ride not found."));

        if (!ride.getDriverAuthUserId().equals(driverAuthUserId)) {
            throw new UnauthorizedRideAccessException(
                    "You are not allowed to accept this request.");
        }

        if (ride.getStatus() != RideStatus.AVAILABLE) {
            throw new RideNotAvailableException(
                    "Ride is no longer available.");
        }

        if (rideRequest.getStatus() != RideRequestStatus.PENDING) {
            throw new InvalidRideRequestStateException(
                    "Only pending requests can be accepted.");
        }

        // Accept selected request
        rideRequest.setStatus(RideRequestStatus.ACCEPTED);
        ride.setStatus(RideStatus.BOOKED);

        Ride savedRide = rideRepository.save(ride);

        RideRequest savedRideRequest =
                rideRequestRepository.save(rideRequest);

        // Reject all other pending requests
        List<RideRequest> pendingRequests =
                rideRequestRepository.findByRideIdAndStatus(
                        savedRide.getRideId(),
                        RideRequestStatus.PENDING
                );

        for (RideRequest request : pendingRequests) {
            request.setStatus(RideRequestStatus.REJECTED);
        }

        rideRequestRepository.saveAll(pendingRequests);

        // Fetch driver details
        UserSummaryDto driver = userServiceClient
                        .getUserSummaries(List.of(driverAuthUserId))
                        .get(0);
        System.out.println(driver.getPhoneNumber());

        // Build passenger response
        RequestRideResponseDto response =
                buildRequestRideResponseDto(
                        savedRideRequest,
                        savedRide,
                        driver
                );

        // Notify passenger
        rideEventPublisher.publishRideRequestUpdate(
                savedRideRequest.getPassengerAuthUserId(),
                response,
                "REQUEST_ACCEPTED"
        );

        return modelMapper.map(
                savedRideRequest,
                RideRequestResponseDto.class
        );
    }

    @Override
    public RideRequestResponseDto rejectRideRequest(Integer requestId, UUID driverAuthUserId) {

        RideRequest rideRequest = rideRequestRepository.findById(requestId)
                .orElseThrow(() -> new RideRequestNotFoundException("Ride request not found."));

        Ride ride = rideRepository.findById(rideRequest.getRideId())
                .orElseThrow(() -> new RideNotFoundException("Ride not found."));

        if (!ride.getDriverAuthUserId().equals(driverAuthUserId))
            throw new UnauthorizedRideAccessException("You cannot reject this request.");

        if (rideRequest.getStatus() != RideRequestStatus.PENDING)
            throw new InvalidRideRequestStateException("Only pending requests can be rejected.");

        rideRequest.setStatus(RideRequestStatus.REJECTED);

        RideRequest updatedRequest = rideRequestRepository.save(rideRequest);

        return modelMapper.map(updatedRequest, RideRequestResponseDto.class);
    }

    @Override
    @Transactional
    public void cancelRideRequest(Integer requestId, UUID passengerAuthUserId) {

        RideRequest rideRequest = rideRequestRepository.findById(requestId)
                .orElseThrow(() -> new RideRequestNotFoundException("Ride request not found."));

        if (!rideRequest.getPassengerAuthUserId().equals(passengerAuthUserId))
            throw new UnauthorizedRideAccessException("You cannot cancel this request.");

        Ride ride = rideRepository.findById(rideRequest.getRideId())
                .orElseThrow(() -> new RideNotFoundException("Ride not found."));

        if (rideRequest.getStatus() == RideRequestStatus.ACCEPTED) {
            ride.setStatus(RideStatus.AVAILABLE);
            rideRepository.save(ride);
        }

        rideRequest.setStatus(RideRequestStatus.CANCELLED);

        rideRequestRepository.save(rideRequest);
    }
    @Override
    public List<RideRequestResponseDto> getMyRideRequests(UUID passengerAuthUserId) {

        return rideRequestRepository
                .findByPassengerAuthUserId(passengerAuthUserId)
                .stream()
                .map(request -> modelMapper.map(request, RideRequestResponseDto.class))
                .toList();
    }

    public List<RequestRideResponseDto> getMyActiveRequests(UUID passengerAuthUserId) {

        List<RideRequest> requests = rideRequestRepository
                .findByPassengerAuthUserIdAndStatusIn(
                        passengerAuthUserId,
                        List.of(
                                RideRequestStatus.PENDING,
                                RideRequestStatus.ACCEPTED
                        )
                );

        if (requests.isEmpty()) {
            return List.of();
        }

        // Fetch all rides
        List<Integer> rideIds = requests.stream()
                .map(RideRequest::getRideId)
                .toList();

        List<Ride> rides = rideRepository.findAllById(rideIds);

        Map<Integer, Ride> rideMap = rides.stream()
                .collect(Collectors.toMap(
                        Ride::getRideId,
                        Function.identity()
                ));

        // Fetch all rider profiles in one Feign call
        List<UUID> riderIds = rides.stream()
                .map(Ride::getDriverAuthUserId)
                .distinct()
                .toList();

        List<UserSummaryDto> users =
                userServiceClient.getUserSummaries(riderIds);

        Map<UUID, UserSummaryDto> userMap = users.stream()
                .collect(Collectors.toMap(
                        UserSummaryDto::getAuthUserId,
                        Function.identity()
                ));


        return requests.stream()
                .map(request -> {

                    Ride ride = rideMap.get(request.getRideId());

                    UserSummaryDto driver =
                            userMap.get(ride.getDriverAuthUserId());

                    return buildRequestRideResponseDto(
                            request,
                            ride,
                            driver
                    );
                })
                .toList();
    }
    private RequestRideResponseDto buildRequestRideResponseDto(
            RideRequest request,
            Ride ride,
            UserSummaryDto driver) {

        return RequestRideResponseDto.builder()

                // Request
                .requestId(request.getRequestId())
                .status(request.getStatus())
                .requestedAt(request.getRequestedAt())
                .matchPercentage(request.getMatchPercentage())

                // Ride
                .rideId(ride.getRideId())
                .rideStatus(ride.getStatus())
                .driverAuthUserId(ride.getDriverAuthUserId())
                .source(ride.getSource())
                .destination(ride.getDestination())
                .departureTime(ride.getDepartureTime())
                .ridePrice(ride.getRidePrice())
                .riderEncodedPolyline(ride.getEncodedPolyline())
                .passengerEncodedPolyline(request.getPassengerEncodedPolyline())

                // Driver
                .driverName(driver.getFirstName() + " " + driver.getLastName())
                .driverPhoneNumber(driver.getPhoneNumber())
                .driverProfilePicture(driver.getProfilePictureUrl())

                // Vehicle
                .vehicleNumber(driver.getVehicle() != null ? driver.getVehicle().getVehicleNumber() : null)
                .vehicleModel(driver.getVehicle() != null ? driver.getVehicle().getModel() : null)
                .vehicleColor(
                        driver.getVehicle() != null
                                ? driver.getVehicle().getColor()
                                : null
                )

                .build();
    }

    @Override
    public List<RideRequestResponseDto> getRideRequests(UUID driverAuthUserId) {

        Ride ride = rideRepository.findByDriverAuthUserIdAndStatus(driverAuthUserId, RideStatus.AVAILABLE)
                .orElseThrow(() -> new RideNotFoundException("No active ride found."));

        return rideRequestRepository
                .findByRideIdAndStatus(ride.getRideId(),RideRequestStatus.PENDING)
                .stream()
                .map(request -> modelMapper.map(request, RideRequestResponseDto.class))
                .toList();
    }
}
