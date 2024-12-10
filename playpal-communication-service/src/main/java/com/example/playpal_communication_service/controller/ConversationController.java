package com.example.playpal_communication_service.controller;

import com.example.playpal_communication_service.dto.ConversationDTO;
import com.example.playpal_communication_service.dto.MessageDTO;
import com.example.playpal_communication_service.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping
    public ConversationDTO createConversation(@RequestBody List<Long> userIds, String groupName) {
        return conversationService.createConversation(userIds, groupName);
    }

    @GetMapping("/all")
    public List<ConversationDTO> getAllConversations() {
        return conversationService.getAllConversations();
    }



    @GetMapping("/{userId}/conversations")
    public List<ConversationDTO> getUserConversations(@PathVariable Long userId) {
        return conversationService.getConversationsForUser(userId);
    }

    /*

    @GetMapping("/{userId}")
    public List<ConversationDTO> getConversationsForUser(@PathVariable Long userId) {
        return conversationService.getConversationsForUser(userId);
    }
*/
    @PostMapping("/messages")
    public MessageDTO sendMessage(@RequestBody MessageDTO messageDTO, Long conversationId) {
        return conversationService.sendMessage(messageDTO);
    }

    @GetMapping("/{conversationId}/details")
    public ConversationDTO getConversationWithMessages(@PathVariable Long conversationId) {
        return conversationService.getConversationWithMessages(conversationId);
    }

    @GetMapping("/{conversationId}/messages")
    public List<MessageDTO> getMessagesInConversation(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000000") int size
    ) {
        return conversationService.getMessagesInConversation(conversationId, page, size);
    }


}
