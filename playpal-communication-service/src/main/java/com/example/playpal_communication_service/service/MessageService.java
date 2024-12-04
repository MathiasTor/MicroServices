package com.example.playpal_communication_service.service;

import com.example.playpal_communication_service.dto.MessageDTO;
import com.example.playpal_communication_service.model.Message;
import com.example.playpal_communication_service.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public MessageDTO sendMessage(MessageDTO messageDTO) {
        Message message = new Message();
        message.setSenderId(messageDTO.getSenderId());
        message.setReceiverId(messageDTO.getReceiverId());
        message.setContent(messageDTO.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);

        Message savedMessage = messageRepository.save(message);
        return toDTO(savedMessage);
    }

    public List<MessageDTO> getMessagesForReceiver(Long receiverId) {
        List<Message> messages = messageRepository.findByReceiverId(receiverId);
        return messages.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setRead(true);
        messageRepository.save(message);
    }

    private MessageDTO toDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSenderId());
        dto.setReceiverId(message.getReceiverId());
        dto.setContent(message.getContent());
        dto.setRead(message.isRead());
        dto.setTimestamp(message.getTimestamp());
        return dto;
    }
}