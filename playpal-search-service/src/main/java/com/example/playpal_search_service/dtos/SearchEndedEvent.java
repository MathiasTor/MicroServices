package com.example.playpal_search_service.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchEndedEvent {
    private Long searchId;
    private Long userId;
    private String title;
    private String description;
    private List<Long> approvedUsers;

    // Constructors, Getters, and Setters
    public SearchEndedEvent(Long searchId, Long userId, String title, String description, List<Long> approvedUsers) {
        this.searchId = searchId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.approvedUsers = approvedUsers;
    }
}
