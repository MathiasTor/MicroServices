package com.example.playpalleaderboardservice.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Leaderboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long userId;

    private String runescapeName;

    private int weeklyToaKC;
    private int weeklyCoxKC;
    private int weeklyTobKC;


    private int totalRaids;

    private int rank;

    @Column(name ="last_updated")
    private LocalDateTime lastUpdated;


}
