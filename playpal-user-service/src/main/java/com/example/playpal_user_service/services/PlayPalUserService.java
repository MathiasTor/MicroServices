package com.example.playpal_user_service.services;

import com.example.playpal_user_service.model.PlayPalUser;
import com.example.playpal_user_service.repository.PlayPalUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayPalUserService {

    private final PlayPalUserRepository playPalUserRepository;

    @Autowired
    public PlayPalUserService(PlayPalUserRepository playPalUserRepository) {
        this.playPalUserRepository = playPalUserRepository;
    }

    public List<PlayPalUser> getAllUsers() {
        return playPalUserRepository.findAll();
    }

    public Optional<PlayPalUser> getUserById(Long id) {
        return playPalUserRepository.findById(id);
    }

    public Optional<PlayPalUser> getUserByUsername(String username) {
        return playPalUserRepository.findByUsername(username);
    }

    public PlayPalUser createUser(PlayPalUser user) {
        return playPalUserRepository.save(user);
    }

    public PlayPalUser updateUser(Long id, PlayPalUser userDetails) {
        return playPalUserRepository.findById(id).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setPassword(userDetails.getPassword());
            user.setEmail(userDetails.getEmail());
            return playPalUserRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    public void deleteUser(Long id) {
        if (playPalUserRepository.existsById(id)) {
            playPalUserRepository.deleteById(id);
        } else {
            throw new RuntimeException("User not found with id " + id);
        }
    }
}
