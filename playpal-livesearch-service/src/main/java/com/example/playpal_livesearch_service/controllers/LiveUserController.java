package com.example.playpal_livesearch_service.controllers;

import com.example.playpal_livesearch_service.models.LiveUser;
import com.example.playpal_livesearch_service.services.LiveUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/live-search")
@RequiredArgsConstructor
public class LiveUserController {

    private final LiveUserService liveUserService;

    @PostMapping("/enable/{userId}")
    public LiveUser goLive(
            @PathVariable Long userId,
            @RequestBody LiveUser liveUser
    ) {
        liveUser.setUserId(userId);
        liveUser.setLive(true); // Explicitly mark the user as live
        return liveUserService.goLive(
                liveUser.getUserId(),
                liveUser.getTags(),
                liveUser.getVideoGame()
        );
    }

    @PostMapping("/stop-live/{userId}")
    public void stopLive(@PathVariable Long userId) {
        liveUserService.stopLive(userId);
    }

    @GetMapping("/live-users")
    public List<LiveUser> getLiveUsers() {
        return liveUserService.getLiveUsers();
    }
}
