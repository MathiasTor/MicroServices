package com.example.Playpal_profile_service.controller;

import com.example.Playpal_profile_service.model.PlaypalProfile;
import com.example.Playpal_profile_service.model.Vote;
import com.example.Playpal_profile_service.service.ProfileService;
import com.example.Playpal_profile_service.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
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
}