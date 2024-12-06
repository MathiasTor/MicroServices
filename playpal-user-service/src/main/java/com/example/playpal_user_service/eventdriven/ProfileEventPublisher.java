package com.example.playpal_user_service.eventdriven;

import com.example.playpal_user_service.model.PlayPalUser;
import com.example.playpal_user_service.model.ProfileDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProfileEventPublisher {

    private final AmqpTemplate amqpTemplate;
    private final String exchangeName;

    public ProfileEventPublisher(AmqpTemplate amqpTemplate, @Value("${amqp.exchange.name}") String exchangeName) {
        this.amqpTemplate = amqpTemplate;
        this.exchangeName = exchangeName;
    }

    public void publishGroupCreatedEvent(PlayPalUser playPalUser) {
        // Convert PlayPalUser to ProfileDTO
        ProfileDTO profileDTO = dtoConverter(playPalUser);

        // Log and publish the event
        log.info("Publishing profile.created event: {}", profileDTO);
        amqpTemplate.convertAndSend(exchangeName, "profile.created", profileDTO);
    }

    // DTO Converter Method
    public ProfileDTO dtoConverter(PlayPalUser playPalUser) {
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUserId(playPalUser.getId());
        profileDTO.setName(playPalUser.getUsername());
        return profileDTO;
    }
}
