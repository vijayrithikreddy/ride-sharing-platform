package com.rideshare.rideservice.websocket;

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
}