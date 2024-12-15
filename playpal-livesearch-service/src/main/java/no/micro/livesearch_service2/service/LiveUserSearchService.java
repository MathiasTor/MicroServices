package no.micro.livesearch_service2.service;

import no.micro.livesearch_service2.model.LiveUserSearch;
import no.micro.livesearch_service2.repo.LiveUserSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LiveUserSearchService {
    @Autowired
    private LiveUserSearchRepository liveUserSearchRepository;

    public List<LiveUserSearch> getLiveUserSearches() {
        return liveUserSearchRepository.findAll();
    }

    public LiveUserSearch goLive(Long userId) {
        deactivatePreviousEntries(userId);

        // Attempt to find a match
        LiveUserSearch matchedEntry = findMatch(userId);
        if (matchedEntry != null) {
            return matchedEntry;
        } else {
            return newLiveUserSearch(userId);
        }
    }

    private LiveUserSearch newLiveUserSearch(Long userId) {
        LiveUserSearch liveUserSearch = new LiveUserSearch();
        liveUserSearch.setUserId(userId);
        liveUserSearch.setActive(1);
        return liveUserSearchRepository.save(liveUserSearch);
    }

    public LiveUserSearch findMatch(Long userId) {
        List<LiveUserSearch> liveUserSearches = liveUserSearchRepository.findAll();

        for (LiveUserSearch liveUserSearch : liveUserSearches) {
            if (!liveUserSearch.getUserId().equals(userId) && liveUserSearch.getActive() == 1) {
                liveUserSearch.setActive(0);
                liveUserSearch.setMatchedUserId(userId);

                String groupName = generateGroupName(userId, liveUserSearch.getUserId());

                //Create a group for the matched users
                pushGroupToService(List.of(userId, liveUserSearch.getUserId()), "Runescape", List.of(""), groupName);

                return liveUserSearchRepository.save(liveUserSearch);
            }
        }

        return null;
    }

    private String generateGroupName(Long userId, Long matchId){
        if (userId < matchId) {
            return userId + "-" + matchId + "-Matched Group";
        } else {
            return matchId + "-" + userId + "-Matched Group";
        }
    }

    private void deactivatePreviousEntries(Long userId) {
        List<LiveUserSearch> userEntries = liveUserSearchRepository.findAll();
        for (LiveUserSearch entry : userEntries) {
            if (entry.getUserId().equals(userId) && entry.getActive() == 1) {
                entry.setActive(0);
                liveUserSearchRepository.save(entry);
            }
        }
    }

    public LiveUserSearch stopLive(Long userId) {
        List<LiveUserSearch> userEntries = liveUserSearchRepository.findAll();
        for (LiveUserSearch entry : userEntries) {
            if (entry.getUserId().equals(userId) && entry.getActive() == 1) {
                entry.setActive(0);
                return liveUserSearchRepository.save(entry);
            }
        }

        return null;
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

    public boolean isMatched(Long id) {
        List<LiveUserSearch> userEntries = liveUserSearchRepository.findAll();
        for (LiveUserSearch entry : userEntries) {
            if (entry.getId().equals(id) && entry.getMatchedUserId() != 0) {
                return true;
            }
        }

        return false;
    }

    @Value("${api.url}")
    private String apiUrl;

    //POST to group service to create a group
    private void pushGroupToService(List<Long> userIds, String game, List<String> preferences, String groupName) {
        Map<String, Object> groupPayload = new HashMap<>();
        groupPayload.put("groupName", groupName);
        groupPayload.put("groupDescription", String.join(", ", preferences));
        groupPayload.put("userIds", userIds);

        String apiEndpoint = apiUrl + "/group/api/group/new";
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
}
