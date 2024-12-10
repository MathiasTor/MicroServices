package com.example.playpal_livesearch_service.services;

import com.example.playpal_livesearch_service.models.LiveUser;

import java.time.LocalDateTime;
import java.util.List;

public interface LiveUserService {
    LiveUser goLive(Long userId, List<String> tags, String videoGame);
    void stopLive(Long userId);
    List<LiveUser> getLiveUsers();
    void removeExpiredUsers(); // For cleaning expired live users
}