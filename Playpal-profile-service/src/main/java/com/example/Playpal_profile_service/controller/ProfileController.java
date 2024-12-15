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
    public ResponseEntity<?> proxyGenerateImage(@RequestBody Map<String, String> body, @PathVariable Long userId) {
        PlaypalProfile profile = profileService.getProfileByUserId(userId).orElse(null);

        if (profile == null) {
            return ResponseEntity.badRequest().body("Profile not found.");
        }

        String endpointURL = "https://dall-e-main.replit.app/generate-image";
        RestTemplate restTemplate = new RestTemplate();
        try{
            Map<String, Object> response = restTemplate.postForObject(endpointURL, body, Map.class);
            List<String> imageUrls = (List<String>) response.get("image_urls");
            String imageUrl = imageUrls.get(0);

            profileService.updateProfileImage(userId, imageUrl);

            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to generate image.");
        }
    }
}