package com.rideshare.notificationservice.consumer;

import com.rideshare.notificationservice.config.RabbitMQConfig;
import com.rideshare.notificationservice.event.OtpNotificationEvent;
import com.rideshare.notificationservice.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OtpNotificationConsumer {

    private final EmailNotificationService emailNotificationService;

    @RabbitListener(
            queues = RabbitMQConfig.OTP_QUEUE
    )
    public void consumeOtp(
            OtpNotificationEvent event
    ) {

        log.info(
                "OTP notification received for: {}",
                event.getEmail()
        );

        emailNotificationService.sendOtpEmail(event);

        log.info(
                "OTP email sent successfully to: {}",
                event.getEmail()
        );
    }
}