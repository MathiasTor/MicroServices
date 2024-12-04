package com.example.playpal_communication_service.controller;

import com.example.playpal_communication_service.dto.MessageDTO;
import com.example.playpal_communication_service.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public MessageDTO sendMessage(@RequestBody MessageDTO messageDTO) {
        return messageService.sendMessage(messageDTO);
    }

    @GetMapping("/{receiverId}")
    public List<MessageDTO> getMessages(@PathVariable Long receiverId) {
        return messageService.getMessagesForReceiver(receiverId);
    }

    @PutMapping("/{messageId}/read")
    public void markMessageAsRead(@PathVariable Long messageId) {
        messageService.markAsRead(messageId);
    }
}