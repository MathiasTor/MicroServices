package com.example.playpal_search_service.EventDriven;

import com.example.playpal_search_service.dtos.SearchEndedEvent;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SearchEventPublisher {

    private final AmqpTemplate amqpTemplate;
    private final String exchangeName;

    public SearchEventPublisher(AmqpTemplate amqpTemplate, @Value("${amqp.exchange.name}") String exchangeName) {
        this.amqpTemplate = amqpTemplate;
        this.exchangeName = exchangeName;
    }

    public void publishSearchEndedEvent(SearchEndedEvent event) {
        log.info("Publishing search.ended event: {}", event);;
        amqpTemplate.convertAndSend(exchangeName, "search.ended", event);
    }

}
