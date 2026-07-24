package com.rideshare.rideservice.service;

import com.rideshare.rideservice.dto.RideRequestResponseDto;
import com.rideshare.rideservice.entity.Ride;
import com.rideshare.rideservice.entity.RideRequest;
import com.rideshare.rideservice.enums.RideRequestStatus;
import com.rideshare.rideservice.enums.RideStatus;
import com.rideshare.rideservice.exception.*;
import com.rideshare.rideservice.repository.RideRepository;
import com.rideshare.rideservice.repository.RideRequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RideRequestServiceImpl implements RideRequestService{
    private final RideRequestRepository rideRequestRepository;
    private final ModelMapper modelMapper;
    private final RideRepository rideRepository;

    @Override
    public RideRequestResponseDto requestRide(Integer rideId, UUID passengerAuthUserId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found."));

        if (ride.getStatus() != RideStatus.AVAILABLE) {
            throw new RideNotAvailableException("Ride is not available.");
        }

        if (ride.getDriverAuthUserId().equals(passengerAuthUserId)) {
            throw new InvalidRideRequestException(
                    "You cannot request your own ride.");
        }

        if (rideRequestRepository.existsByRideIdAndPassengerAuthUserId(rideId, passengerAuthUserId)) {
            throw new RideRequestAlreadyExistsException("Ride request already exists.");
        }

        RideRequest rideRequest = RideRequest.builder()
                .rideId(rideId)
                .passengerAuthUserId(passengerAuthUserId)
                .build();

        RideRequest savedRideRequest = rideRequestRepository.save(rideRequest);

        return modelMapper.map(savedRideRequest, RideRequestResponseDto.class);
    }

    @Override
    @Transactional
    public RideRequestResponseDto acceptRideRequest(Integer requestId, UUID driverAuthUserId) {

        RideRequest rideRequest = rideRequestRepository.findById(requestId)
                .orElseThrow(() -> new RideRequestNotFoundException("Ride request not found."));

        Ride ride = rideRepository.findById(rideRequest.getRideId())
                .orElseThrow(() -> new RideNotFoundException("Ride not found."));

        if (!ride.getDriverAuthUserId().equals(driverAuthUserId))
            throw new UnauthorizedRideAccessException("You are not allowed to accept this request.");

        if (ride.getStatus() != RideStatus.AVAILABLE)
            throw new RideNotAvailableException("Ride is no longer available.");

        if (rideRequest.getStatus() != RideRequestStatus.PENDING)
            throw new InvalidRideRequestStateException("Only pending requests can be accepted.");

        rideRequest.setStatus(RideRequestStatus.ACCEPTED);
        ride.setStatus(RideStatus.BOOKED);

        rideRepository.save(ride);
        rideRequestRepository.save(rideRequest);

        // Reject all other pending requests
        List<RideRequest> pendingRequests = rideRequestRepository.findByRideIdAndStatus(ride.getRideId(), RideRequestStatus.PENDING);

        for (RideRequest request : pendingRequests) {
            request.setStatus(RideRequestStatus.REJECTED);
        }

        rideRequestRepository.saveAll(pendingRequests);

        return modelMapper.map(rideRequest, RideRequestResponseDto.class);
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

    @Override
    public List<RideRequestResponseDto> getRideRequests(UUID driverAuthUserId) {

        Ride ride = rideRepository.findByDriverAuthUserIdAndStatus(driverAuthUserId, RideStatus.AVAILABLE)
                .orElseThrow(() ->
                        new RideNotFoundException("No active ride found."));

        return rideRequestRepository
                .findByRideIdAndStatus(
                        ride.getRideId(),
                        RideRequestStatus.PENDING)
                .stream()
                .map(request ->
                        modelMapper.map(request, RideRequestResponseDto.class))
                .toList();
    }
}
