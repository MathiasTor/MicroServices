package com.example.playpal_communication_service.controller;

import com.example.playpal_communication_service.dto.MessageDTO;
import com.example.playpal_communication_service.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final ConversationService conversationService;

    @MessageMapping("/sendMessage/{conversationId}")
    @SendTo("/topic/conversations/{conversationId}")
    public MessageDTO sendMessage(MessageDTO messageDTO) {
        log.info("Received message: {}", messageDTO, "yes");
        System.out.println("Received message: " + messageDTO + "yes");
        // Persist the message using the service

        return conversationService.sendMessage(messageDTO);    }


}