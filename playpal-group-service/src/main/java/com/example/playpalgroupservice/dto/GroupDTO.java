package com.example.playpalgroupservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GroupDTO {
    private Long groupId; // ID of the group
    private List<Long> userIds; // List of participant IDs in the group
    private String groupName; // Name of the group
}
