package com.example.playpal_communication_service.eventdriven;

import com.example.playpal_communication_service.dto.ConversationDTO;
import com.example.playpal_communication_service.dto.GroupDTO;
import com.example.playpal_communication_service.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupEventHandler {

    private final ConversationService conversationService;

    @RabbitListener(queues = "${amqp.queue.created.name}")
    public void handleGroupCreatedEvent(GroupDTO groupDTO) {
        log.info("Received group.created event: {}", groupDTO);

        // Create a new conversation for the group participants
        try {
            ConversationDTO conversation = conversationService.createConversation(groupDTO.getParticipantIds());
            log.info("Conversation created for group {}: {}", groupDTO.getGroupId(), conversation);
        } catch (Exception e) {
            log.error("Failed to create conversation for group {}: {}", groupDTO.getGroupId(), e.getMessage());
        }
    }
}
