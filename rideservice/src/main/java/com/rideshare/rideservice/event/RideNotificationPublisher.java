package com.rideshare.rideservice.event;

import com.rideshare.rideservice.config.RabbitMQConfig;
import com.rideshare.rideservice.event.RideNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RideNotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishRideStarted(
            String email,
            Long rideId
    ) {

        RideNotificationEvent event =
                RideNotificationEvent.builder()
                        .email(email)
                        .rideId(rideId)
                        .eventType("RIDE_STARTED")
                        .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.RIDE_STARTED_ROUTING_KEY,
                event
        );
    }

    public void publishRideCompleted(
            String email,
            Long rideId
    ) {

        RideNotificationEvent event =
                RideNotificationEvent.builder()
                        .email(email)
                        .rideId(rideId)
                        .eventType("RIDE_COMPLETED")
                        .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.RIDE_COMPLETED_ROUTING_KEY,
                event
        );
    }
}