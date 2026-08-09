package com.rideshare.authservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
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
    public Binding otpBinding(
            Queue otpQueue,
            TopicExchange notificationExchange
    ) {
        return BindingBuilder
                .bind(otpQueue)
                .to(notificationExchange)
                .with(OTP_ROUTING_KEY);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(
            ConnectionFactory connectionFactory,
            TopicExchange notificationExchange,
            Queue otpQueue,
            Binding otpBinding
    ) {

        RabbitAdmin rabbitAdmin =
                new RabbitAdmin(connectionFactory);

        rabbitAdmin.declareExchange(
                notificationExchange
        );

        rabbitAdmin.declareQueue(
                otpQueue
        );

        rabbitAdmin.declareBinding(
                otpBinding
        );

        return rabbitAdmin;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(
            ObjectMapper objectMapper
    ) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}