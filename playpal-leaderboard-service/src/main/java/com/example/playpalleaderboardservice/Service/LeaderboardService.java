package com.example.playpalleaderboardservice.Service;

import com.example.playpalleaderboardservice.DTO.RunescapeCharDTO;
import com.example.playpalleaderboardservice.Model.Leaderboard;
import com.example.playpalleaderboardservice.Repository.LeaderboardRepo;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class LeaderboardService {

    private final LeaderboardRepo leaderboardRepo;
    private final RestTemplate restTemplate;

    public LeaderboardService(LeaderboardRepo leaderboardRepo, RestTemplate restTemplate) {
        this.leaderboardRepo = leaderboardRepo;
        this.restTemplate = restTemplate;
    }

    @Value("${api.url}")
    private String apiUrl;

    @PostConstruct
    public void init() {
        apiUrl = apiUrl + "/runescape/api/runescape";
    }


    public void updateLeaderboardTotal() {
        String url = apiUrl + "/runescape-chars";
        RunescapeCharDTO[] runescapeChars = restTemplate.getForObject(url, RunescapeCharDTO[].class);

        if (runescapeChars != null) {
            for (RunescapeCharDTO charData : runescapeChars) {
                Leaderboard entry = leaderboardRepo.findByUserId(charData.getUserId())
                        .orElse(new Leaderboard());

                entry.setUserId(charData.getUserId());
                entry.setRunescapeName(charData.getRunescapeName());
                entry.setTotalRaids(charData.getToaKC() + charData.getCoxKC() + charData.getTobKC());
                entry.setLastUpdated(LocalDateTime.now());

                leaderboardRepo.save(entry);
            }
        }
    }


    public void updateLeaderboardWeekly() {
        String url = apiUrl + "/runescape-chars";
        RunescapeCharDTO[] runescapeChars = restTemplate.getForObject(url, RunescapeCharDTO[].class);

        if (runescapeChars != null) {
            for (RunescapeCharDTO charData : runescapeChars) {
                Leaderboard entry = leaderboardRepo.findByUserId(charData.getUserId())
                        .orElse(new Leaderboard());


                if (entry.getBaselineToaKC() == 0 && entry.getBaselineCoxKC() == 0 && entry.getBaselineTobKC() == 0) {
                    entry.setBaselineToaKC(charData.getToaKC());
                    entry.setBaselineCoxKC(charData.getCoxKC());
                    entry.setBaselineTobKC(charData.getTobKC());
                }


                int weeklyToaProgress = charData.getToaKC() - entry.getBaselineToaKC();
                int weeklyCoxProgress = charData.getCoxKC() - entry.getBaselineCoxKC();
                int weeklyTobProgress = charData.getTobKC() - entry.getBaselineTobKC();


                entry.setWeeklyToaKC(Math.max(weeklyToaProgress, 0));
                entry.setWeeklyCoxKC(Math.max(weeklyCoxProgress, 0));
                entry.setWeeklyTobKC(Math.max(weeklyTobProgress, 0));


                entry.setTotalRaids(charData.getToaKC() + charData.getCoxKC() + charData.getTobKC());
                entry.setRunescapeName(charData.getRunescapeName());
                entry.setLastUpdated(LocalDateTime.now());

                leaderboardRepo.save(entry);
            }
        }
    }







    public void resetWeeklyStats() {
        String url = apiUrl + "/runescape-chars";
        RunescapeCharDTO[] runescapeChars = restTemplate.getForObject(url, RunescapeCharDTO[].class);

        if (runescapeChars != null) {
            for (RunescapeCharDTO charData : runescapeChars) {
                Leaderboard entry = leaderboardRepo.findByUserId(charData.getUserId())
                        .orElse(new Leaderboard());


                entry.setBaselineToaKC(charData.getToaKC());
                entry.setBaselineCoxKC(charData.getCoxKC());
                entry.setBaselineTobKC(charData.getTobKC());


                entry.setWeeklyToaKC(0);
                entry.setWeeklyCoxKC(0);
                entry.setWeeklyTobKC(0);

                leaderboardRepo.save(entry);
            }
        }
    }




    public List<Leaderboard> getTotalLeaderboard() {
        return leaderboardRepo.findAllByOrderByTotalRaidsDesc();
    }

    public List<Leaderboard> getWeeklyLeaderboard() {
        List<Leaderboard> leaderboardList = leaderboardRepo.findAll();

        return leaderboardList.stream()
                .sorted((a, b) -> {
                    int totalWeeklyRaidsA = a.getWeeklyToaKC() + a.getWeeklyCoxKC() + a.getWeeklyTobKC();
                    int totalWeeklyRaidsB = b.getWeeklyToaKC() + b.getWeeklyCoxKC() + b.getWeeklyTobKC();
                    return Integer.compare(totalWeeklyRaidsB, totalWeeklyRaidsA);
                })
                .toList();
    }

}
