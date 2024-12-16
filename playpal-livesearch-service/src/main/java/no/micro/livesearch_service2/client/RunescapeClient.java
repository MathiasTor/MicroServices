package no.micro.livesearch_service2.client;


import no.micro.livesearch_service2.model.RunescapeCharDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RunescapeClient {

    private final RestTemplate restTemplate;

    @Value("${api.url}")
    private String runescapeApiBaseUrl;

    @Autowired
    public RunescapeClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Fetch stats for a specific user
    public RunescapeCharDTO getStatsForUser(Long userId) {
        String url = runescapeApiBaseUrl + "/runescape/api/runescape/get-stats/" + userId;
        try {
            ResponseEntity<RunescapeCharDTO> response = restTemplate.getForEntity(url, RunescapeCharDTO.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error fetching stats for userId " + userId + ": " + e.getMessage());
            return null;
        }
    }

    // Calculate combined KC for a user
    public int getCombinedKC(Long userId) {
        RunescapeCharDTO stats = getStatsForUser(userId);
        if (stats == null) {
            return 0; // Default to 0 if user stats are unavailable
        }
        return stats.getCoxKC() + stats.getTobKC() + stats.getToaKC();
    }


    public String getRunescapeName(Long userId) {
        RunescapeCharDTO stats = getStatsForUser(userId);
        return stats != null ? stats.getRunescapeName() : null;
    }

}

