package com.example.playpal_communication_service.repository;

import com.example.playpal_communication_service.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByUserIdsContains(Long participantId);

    Conversation findByGroupId(Long groupId);
    Conversation findByGroupName(String groupName);
}
