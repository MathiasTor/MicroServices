package com.example.playpal_livesearch_service.dto;

import com.example.playpal_livesearch_service.models.LiveUser;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class MatchResponseDTO {
    private LiveUser matchedUser;
    private Long groupId;

    public MatchResponseDTO(LiveUser matchedUser, Long groupId) {
        this.matchedUser = matchedUser;
        this.groupId = groupId;
    }

}
