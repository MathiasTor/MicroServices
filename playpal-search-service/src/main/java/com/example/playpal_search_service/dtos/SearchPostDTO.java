package com.example.playpal_search_service.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SearchPostDTO {
    private Long id; // For response only
    private Long userId; // New field for user validation
    private String title;
    private String description;
    private String tags;
    private String videoGame;
    private boolean live;


    private LocalDateTime createdAt; // For response only
    private LocalDateTime updatedAt; // For response only
}