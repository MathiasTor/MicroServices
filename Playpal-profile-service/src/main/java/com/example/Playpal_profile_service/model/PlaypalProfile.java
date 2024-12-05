package com.example.Playpal_profile_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlaypalProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String name; // Will include "#userId" automatically

    @Column(length = 200)
    private String bio;

    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl; // Stores the Dropbox shared URL


    private int upvotes = 0; // Default value
    private int downvotes = 0; // Default value
}
