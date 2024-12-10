package com.example.playpal_communication_service.service;

import com.example.playpal_communication_service.dto.ConversationDTO;
import com.example.playpal_communication_service.dto.MessageDTO;
import com.example.playpal_communication_service.model.Conversation;
import com.example.playpal_communication_service.model.Message;
import com.example.playpal_communication_service.repository.ConversationRepository;
import com.example.playpal_communication_service.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationServiceImplementation implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public ConversationDTO createConversation(List<Long> userIds, String groupName) {
        log.info("Creating conversation with participants: {}", userIds);
        log.info("Conversation name: {}", groupName);


        Conversation conversation = new Conversation();
        conversation.setUserIds(userIds);
        conversation.setGroupName(groupName); //todo: Fetch name from posts that make group?
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        return mapToConversationDTO(conversationRepository.save(conversation));
    }


    @Override
    public List<ConversationDTO> getAllConversations() {
        return conversationRepository.findAll().stream()
                .map(this::mapToConversationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConversationDTO> getConversationsForUser(Long userId) {
        return conversationRepository.findByUserIdsContains(userId).stream()
                .map(this::mapToConversationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ConversationDTO getConversationWithMessages(Long conversationId) {
        // Fetch the conversation
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        // Map the conversation to ConversationDTO
        ConversationDTO conversationDTO = mapToConversationDTO(conversation);

        // Fetch and map the messages in the conversation
        List<MessageDTO> messages = messageRepository.findByConversationId(conversationId, PageRequest.of(0, Integer.MAX_VALUE))
                .stream()
                .map(this::mapToMessageDTO)
                .collect(Collectors.toList());

        // Attach messages to the ConversationDTO
        conversationDTO.setMessages(messages);

        return conversationDTO;
    }


    @Override
    public MessageDTO sendMessage(MessageDTO messageDTO) {
        Conversation conversation = conversationRepository.findById(messageDTO.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderId(messageDTO.getSenderId());
        message.setContent(messageDTO.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);

        MessageDTO savedMessage = mapToMessageDTO(messageRepository.save(message));
        messagingTemplate.convertAndSend("/topic/conversations/" + conversation.getId(), savedMessage);

        return savedMessage;

    }

    @Override
    public List<MessageDTO> getMessagesInConversation(Long conversationId, int page, int size) {
        Page<Message> messages = messageRepository.findByConversationId(conversationId, PageRequest.of(page, size));
        return messages.stream().map(this::mapToMessageDTO).collect(Collectors.toList());
    }

    private ConversationDTO mapToConversationDTO(Conversation conversation) {
        ConversationDTO dto = new ConversationDTO();
        dto.setId(conversation.getId());
        dto.setUserIds(conversation.getUserIds());
        dto.setGroupName(conversation.getGroupName());
        dto.setCreatedAt(conversation.getCreatedAt());
        dto.setUpdatedAt(conversation.getUpdatedAt());
        return dto;
    }

    private MessageDTO mapToMessageDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversation().getId());
        dto.setSenderId(message.getSenderId());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());
        dto.setRead(message.isRead());
        return dto;
    }
}
