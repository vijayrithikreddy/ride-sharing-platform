package com.rideshare.notificationservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String NOTIFICATION_EXCHANGE =
            "notification.exchange";

    public static final String OTP_QUEUE =
            "notification.otp.queue";

    public static final String OTP_ROUTING_KEY =
            "otp";

    @Bean
    public TopicExchange notificationExchange() {

        return new TopicExchange(
                NOTIFICATION_EXCHANGE
        );
    }

    @Bean
    public Queue otpQueue() {

        return new Queue(
                OTP_QUEUE,
                true
        );
    }

    @Bean
    public Jackson2JsonMessageConverter
    jackson2JsonMessageConverter() {

        return new Jackson2JsonMessageConverter();
    }
}