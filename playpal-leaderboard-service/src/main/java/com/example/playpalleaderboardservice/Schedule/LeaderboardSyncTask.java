package com.example.playpalleaderboardservice.Schedule;

import com.example.playpalleaderboardservice.Service.LeaderboardService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LeaderboardSyncTask {

    private final LeaderboardService leaderboardService;

    public LeaderboardSyncTask(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @Scheduled(fixedRate = 3000) // Every Monday at midnight
    public void syncLeaderboardTotalWeekly() {
        leaderboardService.updateLeaderboardTotal();
    }

    @Scheduled(cron = "0 0 0 * * MON") // Every Monday at midnight
    public void syncLeaderboardWeekly() {
        leaderboardService.updateLeaderboardWeekly();
    }

}
