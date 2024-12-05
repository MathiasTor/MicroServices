package com.example.Playpal_profile_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long profileId; // The profile being voted on

    @Column(nullable = false)
    private Long voterId; // The user who voted

    @Enumerated(EnumType.STRING)
    private VoteType voteType; // UPVOTE or DOWNVOTE

    public enum VoteType {
        UPVOTE, DOWNVOTE
    }
}
