package com.example.Playpal_profile_service.service;

import com.example.Playpal_profile_service.model.PlaypalProfile;
import com.example.Playpal_profile_service.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    // Create a new profile
    public PlaypalProfile createProfile(PlaypalProfile profile) {
        PlaypalProfile existingProfile = profileRepository.findByUserId(profile.getUserId()).orElse(null);
        if (existingProfile != null) {
            throw new RuntimeException("A profile already exists for userId: " + profile.getUserId());
        }

        if (profile.getName() != null) {
            profile.setName(profile.getName() + "#" + profile.getUserId());
        } else {
            throw new RuntimeException("Name cannot be null");
        }

        return profileRepository.save(profile);
    }

    // Update an existing profile
    public PlaypalProfile updateProfile(Long userId, PlaypalProfile updatedProfile) {
        PlaypalProfile existingProfile = profileRepository.findByUserId(userId).orElse(null);
        if (existingProfile == null) {
            throw new RuntimeException("Profile doesnt exist: " + userId );
        }


        // Update bio
        if (updatedProfile.getBio() != null) {
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

    public boolean profileExists(Long userId) {
        return profileRepository.findByUserId(userId).isPresent();
    }

    public void updateProfileImage(Long userId, String imagePath) {
        PlaypalProfile profile = profileRepository.findByUserId(userId).orElse(null);
        if (profile == null) {
            throw new RuntimeException("Profile not found: " + userId);
        }

        profile.setProfilePictureUrl(imagePath);

        profileRepository.save(profile);
    }

    // get all profiles
    public List<PlaypalProfile> getAllProfiles() {
        return profileRepository.findAll();
    }

    //Generate AI profile picture
    public ResponseEntity<String> generateAIPicture(Long userId, String prompt) {
        PlaypalProfile profile = getProfileByUserId(userId).orElse(null);

        if (profile == null) {
            return ResponseEntity.badRequest().body("Profile not found.");
        }

        String endpointURL = "https://dall-e-main.replit.app/generate-image";
        RestTemplate restTemplate = new RestTemplate();

        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("prompt", prompt);

            Map<String, Object> response = restTemplate.postForObject(endpointURL, requestBody, Map.class);

            List<String> imageUrls = (List<String>) response.get("image_urls");
            String externalImageUrl = imageUrls.get(0);

            String localImagePath = saveImageLocally(externalImageUrl, userId);
            updateProfileImage(userId, localImagePath);

            return ResponseEntity.ok(localImagePath);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to generate and save image.");
        }
    }

    //Save profile picture locally
    private String saveImageLocally(String imageUrl, Long userId) throws IOException {
        String directoryPath = "./data/images";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String localFileName = "profile_" + userId + "_" + System.currentTimeMillis() + ".png";
        String localFilePath = directoryPath + "/" + localFileName;

        URL url = new URL(imageUrl);
        try (InputStream in = url.openStream();
             FileOutputStream out = new FileOutputStream(localFilePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        // Return the relative path to the saved image
        return localFileName;
    }
}
