package com.rideshare.authservice.event;

import com.rideshare.authservice.config.RabbitMQConfig;
import com.rideshare.authservice.event.OtpNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OtpEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishOtp(OtpNotificationEvent event) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.OTP_ROUTING_KEY,
                event
        );
    }
}