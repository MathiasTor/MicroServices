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
    private String videoGame; // Example: "Overwatch, Valorant, Apex Legends"
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //Live or not section
    private boolean live; // true for live searches, false for static posts
    private LocalDateTime liveStartTime; // Time of going live


}
