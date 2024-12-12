package com.example.playpal_livesearch_service.controllers;

import com.example.playpal_livesearch_service.dto.MatchResponseDTO;
import com.example.playpal_livesearch_service.models.LiveUser;
import com.example.playpal_livesearch_service.services.LiveUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/live-search")
@RequiredArgsConstructor
@Slf4j
public class LiveUserController {

    private final LiveUserService liveUserService;

    @PostMapping("/enable/{userId}")
    public LiveUser goLive(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> requestBody
    ) {
        List<String> tags = (List<String>) requestBody.get("tags");
        String videoGame = (String) requestBody.get("videoGame");

        return liveUserService.goLive(userId, tags, videoGame);
    }

    @PutMapping("/disable/{userId}")
    public void stopLive(@PathVariable Long userId) {
        liveUserService.stopLive(userId);
    }

    @GetMapping("/live-users")
    public List<LiveUser> getLiveUsers() {
        return liveUserService.getLiveUsers();
    }



    @GetMapping("/status/{userId}")
    public ResponseEntity<Map<String, Boolean>> getLiveStatus(@PathVariable Long userId) {
        boolean isLive = liveUserService.findByUserIdAndIsLiveTrue(userId);
        return ResponseEntity.ok(Map.of("isLive", isLive));
    }

    @PostMapping("/match/{userId}")
    public MatchResponseDTO matchUser(@PathVariable Long userId) {
        log.info("Attempting to match user with ID: {}", userId);
        MatchResponseDTO matchResponseDto = liveUserService.matchLiveUser(userId);


        return matchResponseDto;
    }




}
