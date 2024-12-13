package com.example.Playpal_profile_service.eventdriven;

import com.example.Playpal_profile_service.dto.ProfileDTO;
import com.example.Playpal_profile_service.model.PlaypalProfile;
import com.example.Playpal_profile_service.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileEventHandler {

    private final ProfileService profileService;


    @RabbitListener(queues = "${amqp.queue.created.name}")
    public void handleGroupCreatedEvent(ProfileDTO profileDTO) {
        log.info("Received profile.created event: {}", profileDTO);

        // Convert ProfileDTO to PlaypalProfile
        PlaypalProfile playpalProfile = convertToPlaypalProfile(profileDTO);

        // Call service to create the profile
        profileService.createProfile(playpalProfile);
    }


    // Converter method
    private PlaypalProfile convertToPlaypalProfile(ProfileDTO profileDTO) {
        PlaypalProfile playpalProfile = new PlaypalProfile();
        playpalProfile.setUserId(profileDTO.getUserId());
        playpalProfile.setName(profileDTO.getName());
        return playpalProfile;
    }
}
