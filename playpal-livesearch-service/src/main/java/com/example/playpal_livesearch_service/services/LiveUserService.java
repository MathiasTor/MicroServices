package com.example.playpal_livesearch_service.services;

import com.example.playpal_livesearch_service.dto.MatchResponseDTO;
import com.example.playpal_livesearch_service.models.LiveUser;

import java.util.List;

public interface LiveUserService {
    LiveUser goLive(Long userId, List<String> tags, String videoGame);
    void stopLive(Long userId);
    List<LiveUser> getLiveUsers();
    void removeExpiredUsers();
    MatchResponseDTO matchLiveUser(Long userId);
    List<LiveUser> getLiveUsersExcluding(Long userId);
    boolean findByUserIdAndIsLiveTrue(Long userId);
    void setUserLiveStatus(Long userId, boolean isLive);
    LiveUser getMatchStatus(Long userId);


    MatchResponseDTO matchLiveUser2(Long userId);
}