package com.example.playpal_communication_service.repository;

import com.example.playpal_communication_service.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverId(Long receiverId);
}