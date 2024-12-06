package com.example.playpal_search_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class LiveUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private boolean isLive = true;
    private LocalDateTime liveStartTime;

    @ElementCollection
    private List<String> tags = new ArrayList<>();

    private String videoGame;
}