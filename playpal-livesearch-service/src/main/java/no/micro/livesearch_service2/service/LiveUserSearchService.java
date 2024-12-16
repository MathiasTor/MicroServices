package no.micro.livesearch_service2.service;

import no.micro.livesearch_service2.client.RunescapeClient;
import no.micro.livesearch_service2.model.LiveUserSearch;
import no.micro.livesearch_service2.repo.LiveUserSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class LiveUserSearchService {

    @Autowired
    private LiveUserSearchRepository liveUserSearchRepository;

    @Autowired
    private RunescapeClient runescapeClient;

    @Value("${api.url}")
    private String groupApiBaseUrl;

    public List<LiveUserSearch> getLiveUserSearches() {
        return liveUserSearchRepository.findAll();
    }

    public LiveUserSearch goLive(Long userId) {
        deactivatePreviousEntries(userId);

        int combinedKC = runescapeClient.getCombinedKC(userId);
        String username = runescapeClient.getRunescapeName(userId);

        LiveUserSearch liveUserSearch = new LiveUserSearch();
        liveUserSearch.setUserId(userId);
        liveUserSearch.setCombinedKC(combinedKC);
        liveUserSearch.setScaledDownKC(combinedKC); // scaledDownKC to combinedKC
        liveUserSearch.setActive(1);
        liveUserSearch.setReadMessage(0);
        liveUserSearch.setInGameName(username);

        return liveUserSearchRepository.save(liveUserSearch);
    }

    private void deactivatePreviousEntries(Long userId) {
        List<LiveUserSearch> userEntries = liveUserSearchRepository.findByUserIdAndActive(userId, 1);
        for (LiveUserSearch entry : userEntries) {
            entry.setActive(0); // Deactivate the previous entry
            liveUserSearchRepository.save(entry);
        }
    }

    private LiveUserSearch newLiveUserSearch(Long userId, int combinedKC) {
        LiveUserSearch liveUserSearch = new LiveUserSearch();
        liveUserSearch.setUserId(userId);
        liveUserSearch.setCombinedKC(combinedKC); // Set combined KC
        liveUserSearch.setActive(1);
        return liveUserSearchRepository.save(liveUserSearch);
    }

    @Scheduled(fixedRate = 5000)
    public synchronized void runMatchmaking() {
        List<LiveUserSearch> activeUsers = liveUserSearchRepository.findByActive(1);

        // Iterate through the list of active users
        for (int i = 0; i < activeUsers.size(); i++) {
            LiveUserSearch user = activeUsers.get(i);
            int scaledDownKC = user.getScaledDownKC();
            int combinedKC = user.getCombinedKC();

            // Decrease scaledDownKC by 10% until it reaches 50%
            int newScaledKC = scaledDownKC - (combinedKC * 10 / 100);
            user.setScaledDownKC(Math.max(newScaledKC, combinedKC / 2)); // Minimum is 50% of combinedKC
            liveUserSearchRepository.save(user);

            // Attempt to find a match for this user
            findMatch(user, activeUsers, i);
        }
    }



    public LiveUserSearch findMatch(LiveUserSearch user, List<LiveUserSearch> activeUsers, int currentIndex) {
        for (int j = currentIndex + 1; j < activeUsers.size(); j++) {
            LiveUserSearch otherUser = activeUsers.get(j);

            // Skip users thatvalready matched
            if (!otherUser.getUserId().equals(user.getUserId())
                    && otherUser.getMatchedUserId() == 0
                    && isWithinThreshold(user.getScaledDownKC(), user.getCombinedKC(), otherUser.getScaledDownKC())) {

                // Update both users
                otherUser.setActive(0);
                otherUser.setMatchedUserId(user.getUserId());
                otherUser.setReadMessage(0);

                user.setActive(0);
                user.setMatchedUserId(otherUser.getUserId());
                user.setReadMessage(0);

                // Save both users at the same time
                liveUserSearchRepository.saveAll(List.of(user, otherUser));

                // Group creation
                String groupName = generateGroupName(user.getUserId(), otherUser.getUserId());
                pushGroupToService(List.of(user.getUserId(), otherUser.getUserId()), "Runescape", List.of(), groupName);

                // Remove both matched users from the list to prevent future checks
                activeUsers.remove(j); // Remove the matched user
                activeUsers.remove(currentIndex);

                return user;
            }
        }
        return null;
    }

    private boolean isWithinThreshold(int scaledDownKC, int combinedKC, int otherKC) {
        int lowerBound = Math.min(scaledDownKC, combinedKC);
        int upperBound = Math.max(scaledDownKC, combinedKC);
        return otherKC >= lowerBound && otherKC <= upperBound;
    }


    private String generateGroupName(Long userId, Long matchId) {
        if (userId < matchId) {
            return userId + "-" + matchId + "-Matched Group";
        } else {
            return matchId + "-" + userId + "-Matched Group";
        }
    }


    public String getUnreadMatch(Long userId) {
        List<LiveUserSearch> unreadMatches = liveUserSearchRepository.findUnreadMatches(userId);

        if (!unreadMatches.isEmpty()) {
            LiveUserSearch match = unreadMatches.get(0); // Fetch the first match

            // Mark the match as read
            match.setReadMessage(1);
            liveUserSearchRepository.save(match);

            return "Match found with User ID: " + match.getMatchedUserId();
        }

        return "No unread matches found.";
    }


    private void pushGroupToService(List<Long> userIds, String game, List<String> preferences, String groupName) {
        Map<String, Object> groupPayload = new HashMap<>();
        groupPayload.put("groupName", groupName);
        groupPayload.put("groupDescription", String.join(", ", preferences));
        groupPayload.put("userIds", userIds);

        String apiEndpoint = groupApiBaseUrl + "/group/api/group/new";
        //String apiEndpoint = "http://gateway:8080/group/api/group/new";
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiEndpoint, groupPayload, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Group pushed successfully: " + response.getBody());
            } else {
                System.err.println("Failed to push group. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error while pushing group: " + e.getMessage());
        }
    }


    public LiveUserSearch stopLive(Long userId) {
        List<LiveUserSearch> userEntries = liveUserSearchRepository.findByUserIdAndActive(userId, 1);
        for (LiveUserSearch entry : userEntries) {
            entry.setActive(0);
            entry.setScaledDownKC(entry.getCombinedKC());
            return liveUserSearchRepository.save(entry);
        }
        return null;
    }

    public List<LiveUserSearch> getUnreadMatches(Long userId) {
        return liveUserSearchRepository.findUnreadMatches(userId);
    }


    public boolean isUserLive(Long userId) {
        List<LiveUserSearch> userEntries = liveUserSearchRepository.findAll();
        for (LiveUserSearch entry : userEntries) {
            if (entry.getUserId().equals(userId) && entry.getActive() == 1) {
                return true;
            }
        }
        return false;









    }

}