package com.example.playpal_user_service.controller;

import com.example.playpal_user_service.model.PlayPalUser;

import com.example.playpal_user_service.model.UserDTO;
import com.example.playpal_user_service.services.PlayPalUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class PlayPalUserController {

    private final PlayPalUserService playPalUserService;

    @Autowired
    public PlayPalUserController(PlayPalUserService playPalUserService) {
        this.playPalUserService = playPalUserService;
    }

    @GetMapping("/validate/{userId}")
    public ResponseEntity<Boolean> validateUser(@PathVariable Long userId) {
        log.info("Validating user with ID: {}", userId);
        return ResponseEntity.ok(userId % 2 == 0); // Mock: Even IDs are valid
    }

    @GetMapping("/{userId}")
    public String getUserName(@PathVariable Long userId) {
        return playPalUserService.getUserName(userId);
    }

    @GetMapping
    public ResponseEntity<List<PlayPalUser>> getAllUsers() {
        return ResponseEntity.ok(playPalUserService.getAllUsers());
    }

    @GetMapping("/specific/{id}")
    public ResponseEntity<PlayPalUser> getUserById(@PathVariable Long id) {
        return playPalUserService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Get id by username
    @GetMapping("/get-id/{username}")
    public ResponseEntity<Long> getIdByUsername(@PathVariable String username) {
        return playPalUserService.getUserByUsername(username)
                .map(user -> ResponseEntity.ok(user.getId()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<PlayPalUser> getUserByUsername(@RequestParam String username) {
        return playPalUserService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PlayPalUser> createUser(@RequestBody UserDTO user) {
        return ResponseEntity.ok(playPalUserService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayPalUser> updateUser(
            @PathVariable Long id,
            @RequestBody UserDTO userDetails
    ) {
        try {
            return ResponseEntity.ok(playPalUserService.updateUser(id, userDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            playPalUserService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/login")
    public ResponseEntity<PlayPalUser> login(@RequestBody UserDTO userDTO) {
        try {
            // Authenticate the user
            PlayPalUser user = playPalUserService.loginUser(userDTO.getUsername(), userDTO.getPassword());

            // Return the user object directly (password excluded)
            return ResponseEntity.ok(user);

        } catch (RuntimeException ex) {
            return ResponseEntity.status(401).body(null); // Unauthorized response
        }
    }





}
