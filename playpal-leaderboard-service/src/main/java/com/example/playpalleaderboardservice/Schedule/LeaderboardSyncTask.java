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

    // Update total stats every 30 seconds
    @Scheduled(fixedRate = 30000)
    public void updateTotalStats() {
        leaderboardService.updateLeaderboardTotal();
        leaderboardService.updateLeaderboardWeekly();
    }


    @Scheduled(cron = "0 0 0 * * MON")
    public void resetWeeklyStats() {
        leaderboardService.resetWeeklyStats();
    }
}
