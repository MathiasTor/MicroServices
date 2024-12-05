package com.example.playpalgroupservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupDTO {
    private Long groupId; // ID of the group
    private List<Long> userIds; // List of participant IDs in the group
}
