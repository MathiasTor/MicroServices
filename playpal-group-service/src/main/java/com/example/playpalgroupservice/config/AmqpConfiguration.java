package com.example.playpalgroupservice.config;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfiguration {
    @Value("${amqp.exchange.name}")
    private String exchangeName;

    @Value("${amqp.queue.stopped.name}")
    private String stoppedQueueName;

    @Value("${amqp.routing-key.search-ended}")
    private String searchEndedRoutingKey;

    @Bean
    public TopicExchange groupTopicExchange() {
        return ExchangeBuilder.topicExchange(exchangeName).durable(true).build();
    }

    @Bean
    public Queue stoppedQueue() {
        return new Queue(stoppedQueueName, true); // Durable queue
    }

    @Bean
    public Binding bindingStoppedQueue(Queue stoppedQueue, TopicExchange groupTopicExchange) {
        return BindingBuilder.bind(stoppedQueue).to(groupTopicExchange).with(searchEndedRoutingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
