package com.example.playpal_communication_service.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;

import java.util.List;

@Getter
@Setter
public class GroupDTO {
    private Long groupId; // ID of the group
    private List<Long> participantIds; // IDs of the participants


}
