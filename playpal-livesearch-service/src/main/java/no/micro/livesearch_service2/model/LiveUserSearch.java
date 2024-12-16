package no.micro.livesearch_service2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
@Entity
@Getter
@Setter
public class LiveUserSearch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private long matchedUserId;

    private int active = 1;

    private int combinedKC; // Original KC that never changes

    private int scaledDownKC; // Future Scaled-down KC

    private int readMessage = 0; // 0 = Unread, 1 = Read

    private String inGameName; // In case we want the RuneScape username
}