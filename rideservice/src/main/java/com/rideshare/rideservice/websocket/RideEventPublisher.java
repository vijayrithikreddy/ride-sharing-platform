package com.rideshare.rideservice.websocket;

import com.rideshare.rideservice.dto.LiveLocationDto;
import com.rideshare.rideservice.dto.RequestRideResponseDto;
import com.rideshare.rideservice.dto.RideLiveLocationDto;
import com.rideshare.rideservice.dto.RideRequestResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RideEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publishNewRideRequest(
            Integer rideId,
            RideRequestResponseDto dto) {

        SocketEvent<RideRequestResponseDto> event =
                new SocketEvent<>(
                        "NEW_REQUEST",
                        dto
                );

        messagingTemplate.convertAndSend(
                "/topic/rides/" + rideId + "/requests",
                event
        );
    }

    public void publishRideRequestUpdate(
            UUID passengerAuthUserId,
            RequestRideResponseDto dto,
            String eventType) {

        SocketEvent<RequestRideResponseDto> event =
                new SocketEvent<>(
                        eventType,
                        dto
                );

        messagingTemplate.convertAndSend(
                "/topic/passenger/" + passengerAuthUserId,
                event
        );
    }

    public void publishRideStarted(
            UUID driverAuthUserId,
            UUID passengerAuthUserId,
            Integer rideId) {

        SocketEvent<Integer> event =
                new SocketEvent<>(
                        "RIDE_STARTED",
                        rideId
                );

        messagingTemplate.convertAndSend(
                "/topic/driver/" + driverAuthUserId,
                event
        );

        messagingTemplate.convertAndSend(
                "/topic/passenger/" + passengerAuthUserId,
                event
        );
    }
    public void publishLiveLocation(
            Integer rideId,
            RideLiveLocationDto dto
    ) {

        SocketEvent<RideLiveLocationDto> event =
                new SocketEvent<>(
                        "LIVE_LOCATION",
                        dto
                );

        messagingTemplate.convertAndSend(
                "/topic/live/" + rideId,
                event
        );

    }
    public void publishRideCompleted(
            UUID driverAuthUserId,
            UUID passengerAuthUserId,
            Integer rideId
    ) {

        SocketEvent<Integer> event =
                new SocketEvent<>(
                        "RIDE_COMPLETED",
                        rideId
                );

        messagingTemplate.convertAndSend(
                "/topic/live/" + rideId,
                event
        );

    }
}