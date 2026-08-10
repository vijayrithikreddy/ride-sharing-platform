package com.rideshare.rideservice.config;

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

    public static final String RIDE_NOTIFICATION_QUEUE =
            "notification.ride.queue";

    public static final String RIDE_STARTED_ROUTING_KEY =
            "ride.started";

    public static final String RIDE_COMPLETED_ROUTING_KEY =
            "ride.completed";

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Queue rideNotificationQueue() {
        return new Queue(
                RIDE_NOTIFICATION_QUEUE,
                true
        );
    }

    @Bean
    public Binding rideStartedBinding(
            Queue rideNotificationQueue,
            TopicExchange notificationExchange
    ) {
        return BindingBuilder
                .bind(rideNotificationQueue)
                .to(notificationExchange)
                .with(RIDE_STARTED_ROUTING_KEY);
    }

    @Bean
    public Binding rideCompletedBinding(
            Queue rideNotificationQueue,
            TopicExchange notificationExchange
    ) {
        return BindingBuilder
                .bind(rideNotificationQueue)
                .to(notificationExchange)
                .with(RIDE_COMPLETED_ROUTING_KEY);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(
            ConnectionFactory connectionFactory,
            TopicExchange notificationExchange,
            Queue rideNotificationQueue,
            Binding rideStartedBinding,
            Binding rideCompletedBinding
    ) {
        RabbitAdmin rabbitAdmin =
                new RabbitAdmin(connectionFactory);

        rabbitAdmin.declareExchange(notificationExchange);

        rabbitAdmin.declareQueue(rideNotificationQueue);

        rabbitAdmin.declareBinding(rideStartedBinding);

        rabbitAdmin.declareBinding(rideCompletedBinding);

        return rabbitAdmin;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}