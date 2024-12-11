package com.example.playpalleaderboardservice.Service;

import com.example.playpalleaderboardservice.DTO.RunescapeCharDTO;
import com.example.playpalleaderboardservice.Model.Leaderboard;
import com.example.playpalleaderboardservice.Repository.LeaderboardRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeaderboardService {

    private final LeaderboardRepo leaderboardRepo;
    private final RestTemplate restTemplate;

    public LeaderboardService(LeaderboardRepo leaderboardRepo, RestTemplate restTemplate) {
        this.leaderboardRepo = leaderboardRepo;
        this.restTemplate = restTemplate;
    }

    private final String runescapeServiceUrl = "http://localhost:8080/runescape/api/runescape";

    public void updateLeaderboardTotal() {
        String url = runescapeServiceUrl + "/runescape-chars";
        RunescapeCharDTO[] runescapeChars = restTemplate.getForObject(url, RunescapeCharDTO[].class);

        if (runescapeChars != null) {
            for (RunescapeCharDTO charData : runescapeChars) {
                Leaderboard entry = leaderboardRepo.findByUserId(charData.getUserId())
                        .orElse(new Leaderboard());

                entry.setUserId(charData.getUserId());
                entry.setRunescapeName(charData.getRunescapeName());
                entry.setWeeklyToaKC(charData.getToaKC());
                entry.setWeeklyCoxKC(charData.getCoxKC());
                entry.setWeeklyTobKC(charData.getTobKC());
                entry.setTotalRaids(charData.getToaKC() + charData.getCoxKC() + charData.getTobKC());
                entry.setLastUpdated(LocalDateTime.now());

                leaderboardRepo.save(entry);
            }
        }
    }

    public void updateLeaderboardWeekly() {
        String url = runescapeServiceUrl + "/runescape-chars";
        RunescapeCharDTO[] runescapeChars = restTemplate.getForObject(url, RunescapeCharDTO[].class);

        if (runescapeChars != null) {
            for (RunescapeCharDTO charData : runescapeChars) {
                Leaderboard entry = leaderboardRepo.findByUserId(charData.getUserId())
                        .orElse(new Leaderboard());

                // Calculate weekly kills
                int weeklyToaKC = charData.getToaKC() - entry.getWeeklyToaKC();
                int weeklyCoxKC = charData.getCoxKC() - entry.getWeeklyCoxKC();
                int weeklyTobKC = charData.getTobKC() - entry.getWeeklyTobKC();

                // Update leaderboard entry
                entry.setUserId(charData.getUserId());
                entry.setRunescapeName(charData.getRunescapeName());
                entry.setWeeklyToaKC(weeklyToaKC > 0 ? weeklyToaKC : 0);
                entry.setWeeklyCoxKC(weeklyCoxKC > 0 ? weeklyCoxKC : 0);
                entry.setWeeklyTobKC(weeklyTobKC > 0 ? weeklyTobKC : 0);
                entry.setTotalRaids(charData.getToaKC() + charData.getCoxKC() + charData.getTobKC());
                entry.setLastUpdated(LocalDateTime.now());

                leaderboardRepo.save(entry);
            }
        }
    }

    public List<Leaderboard> getTotalLeaderboard() {
        return leaderboardRepo.findAllByOrderByTotalRaidsDesc();
    }

    public List<Leaderboard> getWeeklyLeaderboard() {
        return leaderboardRepo.findAllByOrderByWeeklyToaKCDesc();
    }

}
