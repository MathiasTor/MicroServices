package com.example.Playpal_profile_service.service;

import com.example.Playpal_profile_service.model.PlaypalProfile;
import com.example.Playpal_profile_service.repository.ProfileRepository;
import com.example.Playpal_profile_service.validate.ValidationCharacters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    // Create a new profile
    public PlaypalProfile createProfile(PlaypalProfile profile) {
        return profileRepository.save(profile);
    }

    // Update an existing profile
    public PlaypalProfile updateProfile(Long userId, PlaypalProfile updatedProfile) {
        PlaypalProfile existingProfile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for userId: " + userId));

        // Update name
        if (updatedProfile.getName() != null && !updatedProfile.getName().isEmpty()) {
            ValidationCharacters.validateName(updatedProfile.getName());
            existingProfile.setName(ValidationCharacters.formatName(updatedProfile.getName(), userId));
        }

        // Update bio
        if (updatedProfile.getBio() != null) {
            ValidationCharacters.validateBio(updatedProfile.getBio());
            existingProfile.setBio(updatedProfile.getBio());
        }

        // Update profile picture
        if (updatedProfile.getProfilePictureUrl() != null) {
            existingProfile.setProfilePictureUrl(updatedProfile.getProfilePictureUrl());
        }

        return profileRepository.save(existingProfile);
    }

    // Retrieve a profile by userId
    public Optional<PlaypalProfile> getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId);
    }
}
