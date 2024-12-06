package com.example.playpal_user_service.services;

import com.example.playpal_user_service.model.PlayPalUser;
import com.example.playpal_user_service.model.UserDTO;
import com.example.playpal_user_service.repository.PlayPalUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.channels.FileChannel;
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

    public PlayPalUser createUser(UserDTO user) {

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new RuntimeException("Username is required.");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new RuntimeException("Password is required.");
        }

        if (playPalUserRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists.");
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty() &&
                playPalUserRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists.");
        }

        // Create and save the user
        PlayPalUser playPalUser = new PlayPalUser();
        playPalUser.setUsername(user.getUsername());
        playPalUser.setPassword(user.getPassword());
        playPalUser.setEmail(user.getEmail());

        return playPalUserRepository.save(playPalUser);
    }


    public PlayPalUser updateUser(Long id, UserDTO userDetails) {

        // Find the existing user by ID
        PlayPalUser existingUser = playPalUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));

        // Check and update the password if provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(userDetails.getPassword());
        }

        // Check and update the email if provided
        if (userDetails.getEmail() != null && !userDetails.getEmail().isEmpty()) {
            // Check if the new email is already used
            Optional<PlayPalUser> userWithSameEmail = playPalUserRepository.findByEmail(userDetails.getEmail());
            if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(existingUser.getId())) {
                throw new RuntimeException("Email is already in use by another user.");
            }
            existingUser.setEmail(userDetails.getEmail());
        }

        // Save and return the updated user
        return playPalUserRepository.save(existingUser);
    }


    public PlayPalUser loginUser(String username, String password) {
        PlayPalUser user = playPalUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid username or password");
        }

        return user;
    }





    public void deleteUser(Long id) {
        if (playPalUserRepository.existsById(id)) {
            playPalUserRepository.deleteById(id);
        } else {
            throw new RuntimeException("User not found with id " + id);
        }
    }

    public Long getIdByUsername(String username) {
        return playPalUserRepository.findByUsername(username)
                .map(PlayPalUser::getId)
                .orElse(null);
    }

    public String getUserName(Long userId) {
        PlayPalUser user = playPalUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return user.getUsername();
    }
}
