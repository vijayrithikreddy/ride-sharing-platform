package com.rideshare.notificationservice.consumer;

import com.rideshare.notificationservice.event.RideNotificationEvent;
import com.rideshare.notificationservice.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RideNotificationConsumer {

    private final EmailNotificationService emailNotificationService;

    @RabbitListener(queues = "notification.ride.queue")
    public void consumeRideNotification(
            RideNotificationEvent event
    ) {

        log.info(
                "Ride notification received: {}",
                event
        );

        emailNotificationService.sendRideNotification(event);
    }
}