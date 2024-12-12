package com.example.playpal_livesearch_service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class GroupDTO {
    private Long groupID;
    private String groupName;
    private String groupDescription;
    private List<Long> userIds;
}
