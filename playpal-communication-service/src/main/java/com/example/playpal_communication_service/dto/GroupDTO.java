package com.example.playpal_communication_service.dto;

import lombok.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GroupDTO {
    private Long groupId; // ID of the group
    private List<Long> userIds; // IDs of the participants
    private String groupName;


}
