package com.example.playpal_communication_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfiguration {

    // Define the exchange
    @Bean
    public TopicExchange groupTopicExchange(
            @Value("${amqp.exchange.name}") final String exchangeName) {
        return ExchangeBuilder.topicExchange(exchangeName).durable(true).build();
    }

    // Define the queue for group creation events
    @Bean
    public Queue groupCreatedQueue(
            @Value("${amqp.queue.created.name}") final String queueName) {
        return QueueBuilder.durable(queueName).build();
    }

    // Bind the queue to the exchange with a routing key
    @Bean
    public Binding groupCreatedBinding(
            Queue groupCreatedQueue,
            TopicExchange groupTopicExchange) {
        return BindingBuilder
                .bind(groupCreatedQueue)
                .to(groupTopicExchange)
                .with("group.created");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
