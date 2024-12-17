package com.example.Playpal_profile_service.controller;

import com.example.Playpal_profile_service.model.PlaypalProfile;
import com.example.Playpal_profile_service.model.Vote;
import com.example.Playpal_profile_service.service.ProfileService;
import com.example.Playpal_profile_service.service.VoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profiles")
@Slf4j
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private VoteService voteService;

    @PostMapping
    public ResponseEntity<PlaypalProfile> createProfile(@RequestBody PlaypalProfile profile) {
        return ResponseEntity.ok(profileService.createProfile(profile));
    }

    @GetMapping("/all")
    public ResponseEntity<List<PlaypalProfile>> getAllProfiles() {
        return ResponseEntity.ok(profileService.getAllProfiles());
    }


    @PutMapping("/{userId}")
    public ResponseEntity<PlaypalProfile> updateProfile(
            @PathVariable Long userId,
            @RequestBody PlaypalProfile updatedProfile) {
        return ResponseEntity.ok(profileService.updateProfile(userId, updatedProfile));
    }


    @PostMapping("/{profileId}/vote")
    public ResponseEntity<PlaypalProfile> castVote(
            @PathVariable Long profileId,
            @RequestParam Long voterId,
            @RequestParam Vote.VoteType voteType) {
        return ResponseEntity.ok(voteService.castVote(profileId, voterId, voteType));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<PlaypalProfile> getProfile(@PathVariable Long userId) {
        return profileService.getProfileByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/generate-ai-picture/{userId}")
    public ResponseEntity<?> generateAIPicture(@RequestBody Map<String, String> body, @PathVariable Long userId) {
        String imagePrompt = body.get("text");
        if (imagePrompt == null || imagePrompt.isEmpty()) {
            return ResponseEntity.badRequest().body("Image prompt is missing.");
        }
        return profileService.generateAIPicture(userId, imagePrompt);
    }

}