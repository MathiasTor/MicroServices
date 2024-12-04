package com.example.playpal_search_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class SearchPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private String tags; // Example: "competitive, raid, casual"
    private boolean live; // true for live searches, false for static posts
    private String creator; // Username or ID of the creator

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and setters (or use Lombok annotations like @Data or @Getter/@Setter)
}
