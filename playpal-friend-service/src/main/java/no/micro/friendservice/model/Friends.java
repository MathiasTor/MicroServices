package no.micro.friendservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Friends {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ElementCollection
    private List<Long> friendIds = new ArrayList<>();

    @ElementCollection
    private List<Long> blockedIds = new ArrayList<>();

    @ElementCollection
    private List<Long> pendingIds = new ArrayList<>();
}
