package com.example.playpal_communication_service.service;

import com.example.playpal_communication_service.dto.ConversationDTO;
import com.example.playpal_communication_service.dto.MessageDTO;

import java.util.List;

public interface ConversationService {
    List<ConversationDTO> getAllConversations();
    ConversationDTO createConversation(List<Long> userIds, String groupName, Long groupId);
    List<ConversationDTO> getConversationsForUser(Long userId);
    MessageDTO sendMessage(MessageDTO messageDTO);
    List<MessageDTO> getMessagesInConversation(Long conversationId, int page, int size);
    ConversationDTO getConversationWithMessages(Long conversationId);

    ConversationDTO createDMConversation(List<Long> userIds);

    List<ConversationDTO> getDMsForUser(Long userId);

    Long getConversationIdForDM(Long userId1, Long userId2);

    ConversationDTO findByGroupId(Long groupId);

    List<MessageDTO> getMessagesForGroup(Long groupId);
}
