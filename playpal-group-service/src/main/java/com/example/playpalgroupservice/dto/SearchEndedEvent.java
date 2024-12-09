package com.example.playpalgroupservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchEndedEvent {
    private Long searchId;
    private Long userId;
    private String title;
    private String description;

    // Constructors, Getters, and Setters
    public SearchEndedEvent(Long searchId, Long userId, String title, String description) {
        this.searchId = searchId;
        this.userId = userId;
        this.title = title;
        this.description = description;
    }
}
