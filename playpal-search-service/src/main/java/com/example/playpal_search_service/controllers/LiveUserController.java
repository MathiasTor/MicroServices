package com.example.playpal_search_service.controllers;

import com.example.playpal_search_service.model.LiveUser;
import com.example.playpal_search_service.services.LiveUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/live")
@RequiredArgsConstructor
public class LiveUserController {

    private final LiveUserService liveUserService;

    @PostMapping("/enable/{userId}")
    public LiveUser enableLiveStatus(
            @PathVariable Long userId,
            @RequestBody LiveUser liveUser
    ) {
        liveUser.setUserId(userId);
        liveUser.setLive(true);
        return liveUserService.addLiveUser(
                liveUser.getUserId(),
                liveUser.getTags(),
                liveUser.getVideoGame()
        );
    }

    @DeleteMapping("/delete/{userId}")
    public void deleteLiveUser(@PathVariable Long userId) {
        liveUserService.removeLiveUser(userId);
    }

    @PutMapping("/disable/{userId}")
    public List<LiveUser> disableLiveStatus(@PathVariable Long userId) {
        return liveUserService.disableLiveUser(userId);
    }

    @GetMapping
    public List<LiveUser> getAllLiveUsers() {
        return liveUserService.getLiveUsers();
    }
}