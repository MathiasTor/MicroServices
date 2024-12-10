package com.example.playpal_livesearch_service.services;

import com.example.playpal_livesearch_service.models.LiveUser;
import com.example.playpal_livesearch_service.repository.LiveUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiveUserServiceImplementation implements LiveUserService {

    private final LiveUserRepository liveUserRepository;

    @Override
    public LiveUser goLive(Long userId, List<String> tags, String videoGame) {

        List<LiveUser> existingLiveUsers = liveUserRepository.findByUserIdAndIsLiveTrue(userId);
        existingLiveUsers.forEach(liveUser -> {
            liveUser.setLive(false);
            liveUserRepository.save(liveUser);
        });


        // Create a new LiveUser
        LiveUser liveUser = new LiveUser();
        liveUser.setUserId(userId);
        liveUser.setLive(true);
        liveUser.setTags(tags);
        liveUser.setVideoGame(videoGame);
        liveUser.setLiveStartTime(LocalDateTime.now());

        // Save and return the live user
        log.info("User {} is now live with preferences {} for game {}", userId, tags, videoGame);
        return liveUserRepository.save(liveUser);
    }

    @Override
    public void stopLive(Long userId) {
        // Disable live status for the given user
        List<LiveUser> liveUsers = liveUserRepository.findByUserIdAndIsLiveTrue(userId);
        liveUsers.forEach(user -> user.setLive(false));
        liveUserRepository.saveAll(liveUsers);

        log.info("User {} has stopped live matchmaking.", userId);
    }

    @Override
    public List<LiveUser> getLiveUsers() {
        // Retrieve all currently live users
        return liveUserRepository.findByIsLiveTrue();
    }

    @Override
    public void removeExpiredUsers() {
        // Clean up users who have been live for over 30 minutes
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(30);
        List<LiveUser> expiredUsers = liveUserRepository.findByLiveStartTimeBeforeAndIsLiveTrue(expirationTime);

        expiredUsers.forEach(user -> user.setLive(false));
        liveUserRepository.saveAll(expiredUsers);

        log.info("Cleaned up {} expired live users.", expiredUsers.size());
    }
}

