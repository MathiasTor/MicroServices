package com.example.playpal_livesearch_service.services;

import com.example.playpal_livesearch_service.clients.GroupClient;
import com.example.playpal_livesearch_service.dto.MatchResponseDTO;
import com.example.playpal_livesearch_service.models.LiveUser;
import com.example.playpal_livesearch_service.repository.LiveUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiveUserServiceImplementation implements LiveUserService {

    private final LiveUserRepository liveUserRepository;
    private final GroupClient groupClient;


    @Override
    public LiveUser goLive(Long userId, List<String> tags, String videoGame) {
        // Disable any existing live sessions for the user
        List<LiveUser> existingLiveUsers = liveUserRepository.findByUserIdAndIsLiveTrue(userId);
        existingLiveUsers.forEach(liveUser -> {
            liveUser.setLive(false);
            liveUserRepository.save(liveUser);
        });

        // Create a new LiveUser session
        LiveUser liveUser = new LiveUser();
        liveUser.setUserId(userId);
        liveUser.setLive(true);
        liveUser.setTags(tags);
        liveUser.setVideoGame(videoGame);
        liveUser.setLiveStartTime(LocalDateTime.now());

        // Save the new live session to the repository
        liveUserRepository.save(liveUser);

        // Log the user going live
        log.info("User {} is now live with preferences {} for game {}", userId, tags, videoGame);

        return liveUser;
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
    public List<LiveUser> getLiveUsersExcluding(Long userId) {
        return liveUserRepository.findByIsLiveTrue()
                .stream()
                .filter(user -> !user.getUserId().equals(userId)) // Exclude current user
                .collect(Collectors.toList());
    }

    @Override
    public boolean findByUserIdAndIsLiveTrue(Long userId) {
        return liveUserRepository.findByUserIdAndIsLiveTrue(userId).size() > 0;
    }

    @Override
    public void setUserLiveStatus(Long userId, boolean isLive) {
        List<LiveUser> liveUsers = liveUserRepository.findByUserId(userId);
        for (LiveUser liveUser : liveUsers) {
            liveUser.setLive(isLive);
            liveUserRepository.save(liveUser);
        }
        log.info("User {} live status set to: {}", userId, isLive);
    }

    @Override
    public synchronized MatchResponseDTO matchLiveUser(Long userId) {
        // Fetch all currently live users excluding the current user
        log.info("Fetching live users for matching...");
        List<LiveUser> liveUsers = liveUserRepository.findByIsLiveTrue()
                .stream()
                .filter(user -> !user.getUserId().equals(userId))
                .collect(Collectors.toList());

        log.info("Found {} other live users. Attempting to match...", liveUsers.size());

        if (liveUsers.isEmpty()) {
            log.info("No live users available for matching.");
            return null;
        }

        // Match with the first available user (simple logic, can be improved)
        LiveUser matchedUser = liveUsers.get(0);
        LiveUser currentUser = liveUserRepository.findByUserId(userId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No LiveUser found for userId: " + userId));
        log.info("Matched user {} with user {}", userId, matchedUser.getUserId());

        if (currentUser.getGroupId() != null && currentUser.getGroupId().equals(matchedUser.getGroupId())) {
            log.info("Group already exists for users {} and {}: Group ID {}",
                    userId, matchedUser.getUserId(), currentUser.getGroupId());
            return new MatchResponseDTO(matchedUser, currentUser.getGroupId());
        }


        if (matchedUser.getGroupId() != null) {
            log.info("A group was created for the matched user {}. Using existing group ID {}",
                    matchedUser.getUserId(), matchedUser.getGroupId());
            return new MatchResponseDTO(matchedUser, matchedUser.getGroupId());
        }

            // Create a new group if one doesn't exist
            Long groupId = groupClient.createGroup(
                    List.of(userId, matchedUser.getUserId()),
                    matchedUser.getVideoGame(),
                    matchedUser.getTags()
            );

        currentUser.setLive(false);
        currentUser.setMatchedUserId(matchedUser.getUserId());
        matchedUser.setLive(false);
        matchedUser.setMatchedUserId(currentUser.getUserId());
        matchedUser.setVideoGame(matchedUser.getVideoGame());
        liveUserRepository.save(currentUser);
        liveUserRepository.save(matchedUser);



        log.info("Group created with ID: {}", groupId);
        log.info("Matched user {} with user {} in group {}", userId, matchedUser.getUserId(), groupId);

        return new MatchResponseDTO(matchedUser, groupId);
        }


    @Override
    public LiveUser getMatchStatus(Long userId) {

        LiveUser liveUser = liveUserRepository.findByUserId(userId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));


        if (liveUser.getMatchedUserId() != null) {
            log.info("User {} has been matched with user {}", userId, liveUser.getMatchedUserId());
            return liveUser; // User is matched
        }

        if (liveUser.isLive()) {
            log.info("User {} is still searching for a match", userId);
            throw new IllegalStateException("User is still searching and no match is found");
        }

        log.info("User {} stopped searching but no match was found", userId);
        throw new IllegalStateException("User has stopped searching but no match was found");
    }


    @Override
    public void removeExpiredUsers() {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(30);
        List<LiveUser> expiredUsers = liveUserRepository.findByLiveStartTimeBeforeAndIsLiveTrue(expirationTime);

        expiredUsers.forEach(user -> user.setLive(false));
        liveUserRepository.saveAll(expiredUsers);

        log.info("Cleaned up {} expired live users.", expiredUsers.size());
    }
}







