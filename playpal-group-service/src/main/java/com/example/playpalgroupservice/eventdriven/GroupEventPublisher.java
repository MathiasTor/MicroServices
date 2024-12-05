package com.example.playpalgroupservice.eventdriven;

import com.example.playpalgroupservice.dto.GroupDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GroupEventPublisher {

    private final AmqpTemplate amqpTemplate;
    private final String exchangeName;

    public GroupEventPublisher(AmqpTemplate amqpTemplate, @Value("${amqp.exchange.name}") String exchangeName) {
        this.amqpTemplate = amqpTemplate;
        this.exchangeName = exchangeName;
    }

    public void publishGroupCreatedEvent(GroupDTO groupDTO) {
        log.info("Publishing group.created event: {}", groupDTO);
        amqpTemplate.convertAndSend(exchangeName, "group.created", groupDTO);
    }
}
