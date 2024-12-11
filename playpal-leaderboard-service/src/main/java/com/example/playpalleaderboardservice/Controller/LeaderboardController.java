package com.example.playpalleaderboardservice.Controller;

import com.example.playpalleaderboardservice.Model.Leaderboard;
import com.example.playpalleaderboardservice.Service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping("/total")
    public List<Leaderboard> getTotalLeaderboard() {
        return leaderboardService.getTotalLeaderboard();
    }

    @GetMapping("/weekly")
    public List<Leaderboard> getWeeklyLeaderboard() {
        return leaderboardService.getWeeklyLeaderboard();
    }

    @GetMapping("/updateWeekly")
    public void updateWeeklyLeaderboard() {
        leaderboardService.updateLeaderboardWeekly();
    }

    @GetMapping("/updateTotal")
    public void updateTotalLeaderboard() {
        leaderboardService.updateLeaderboardTotal();
    }

}
