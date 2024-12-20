package com.example.playpal_communication_service.controller;

import com.example.playpal_communication_service.dto.ConversationDTO;
import com.example.playpal_communication_service.dto.MessageDTO;
import com.example.playpal_communication_service.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("http://localhost:3000")
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping
    public ConversationDTO createConversation(@RequestBody List<Long> userIds, String groupName, Long groupId) {
        log.info("Creating conversation with participants: {}", userIds);
        return conversationService.createConversation(userIds, groupName, groupId);
    }

    //Create a dm conversation
    @PostMapping("/dm")
    public ConversationDTO createDMConversation(@RequestBody List<Long> userIds) {
        log.info("Creating dm conversation with participants: {}", userIds);
        return conversationService.createDMConversation(userIds);
    }

    //Different way of creating a dm conversation
    @GetMapping("/create/{userId1}/{userId2}")
    public ConversationDTO createDMConversationWithIds(@PathVariable Long userId1, @PathVariable Long userId2) {
        log.info("Creating dm conversation with participants: {} and {}", userId1, userId2);
        return conversationService.createDMConversationWithIds(userId1, userId2);
    }

    //Get dms for user
    @GetMapping("/dm/{userId}")
    public List<ConversationDTO> getDMsForUser(@PathVariable Long userId) {
        log.info("Getting dms for user: {}", userId);
        return conversationService.getDMsForUser(userId);
    }

    //Get conversation id for dm
    @GetMapping("/dm/{userId1}/{userId2}")
    public Long getConversationIdForDM(@PathVariable Long userId1, @PathVariable Long userId2) {
        log.info("Getting conversation id for dm between users: {} and {}", userId1, userId2);
        return conversationService.getConversationIdForDM(userId1, userId2);
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

    //Get conversationId for groupID
    @GetMapping("/group/{groupId}")
    public ConversationDTO getGroupForConvo(@PathVariable Long groupId){
        return conversationService.findByGroupId(groupId);
    }

    //Get messages for group
    @GetMapping("/group/{groupId}/messages")
    public List<MessageDTO> getMessagesForGroup(@PathVariable Long groupId){
        return conversationService.getMessagesForGroup(groupId);
    }
}
