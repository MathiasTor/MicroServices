package com.example.playpal_livesearch_service.models;

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
    private boolean isLive;
    private LocalDateTime liveStartTime;

    @ElementCollection
    private List<String> tags;

    private String videoGame;
}
