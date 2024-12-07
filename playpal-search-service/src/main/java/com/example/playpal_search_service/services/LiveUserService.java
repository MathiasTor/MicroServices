package com.example.playpal_search_service.services;

import com.example.playpal_search_service.model.LiveUser;

import java.util.List;

public interface LiveUserService {
    LiveUser addLiveUser(Long userId, List<String> tags, String videoGame);
    List<LiveUser> disableLiveUser(Long userId);

    void removeLiveUser(Long userId);
    List<LiveUser> getLiveUsers();
    // void cleanExpiredLiveUsers();
}