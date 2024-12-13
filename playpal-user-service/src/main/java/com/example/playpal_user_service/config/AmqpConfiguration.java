package com.example.playpal_user_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfiguration {

    @Bean
    public TopicExchange profileTopicExchange(
            @Value("${amqp.exchange.name}") final String exchangeName) {
        // Declares a topic exchange; will not recreate if it already exists
        return ExchangeBuilder.topicExchange(exchangeName).durable(true).build();
    }

    @Bean
    public Queue profileCreatedQueue(
            @Value("${amqp.queue.created.name}") final String queueName) {
        // Declares a durable queue; will not recreate if it already exists
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public Binding profileCreatedBinding(
            Queue profileCreatedQueue,
            TopicExchange profileTopicExchange) {
        // Declares the binding; will not recreate if it already exists
        return BindingBuilder
                .bind(profileCreatedQueue)
                .to(profileTopicExchange)
                .with("profile.created");
    }


    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
