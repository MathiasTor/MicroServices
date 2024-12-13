package com.example.Playpal_profile_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {


    @Bean
    public TopicExchange profileTopicExchange(
            @Value("${amqp.exchange.name}") final String exchangeName) {
        // Declares a topic exchange; will not recreate if it already exists
        return ExchangeBuilder.topicExchange(exchangeName).durable(true).build();
    }

    // Define the queue for group creation events
    @Bean
    public Queue profileCreatedQueue(
            @Value("${amqp.queue.created.name}") final String queueName) {
        // Declares a durable queue; will not recreate if it already exists
        return QueueBuilder.durable(queueName).build();
    }

    // Bind the queue to the exchange with a routing key
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
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
