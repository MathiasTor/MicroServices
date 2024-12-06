    package com.example.playpal_communication_service.model;

    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.Setter;

    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;

    @Entity
    @Getter
    @Setter
    public class Conversation {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ElementCollection
        private List<Long> participantIds = new ArrayList<>();

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Message> messages = new ArrayList<>();
    }
