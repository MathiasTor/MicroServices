package com.example.playpal_user_service.controller;

import com.example.playpal_user_service.model.PlayPalUser;

import com.example.playpal_user_service.services.PlayPalUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class PlayPalUserController {

    private final PlayPalUserService playPalUserService;

    @Autowired
    public PlayPalUserController(PlayPalUserService playPalUserService) {
        this.playPalUserService = playPalUserService;
    }

    @GetMapping
    public ResponseEntity<List<PlayPalUser>> getAllUsers() {
        return ResponseEntity.ok(playPalUserService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayPalUser> getUserById(@PathVariable Long id) {
        return playPalUserService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<PlayPalUser> getUserByUsername(@RequestParam String username) {
        return playPalUserService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PlayPalUser> createUser(@RequestBody PlayPalUser user) {
        return ResponseEntity.ok(playPalUserService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayPalUser> updateUser(
            @PathVariable Long id,
            @RequestBody PlayPalUser userDetails
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
}
