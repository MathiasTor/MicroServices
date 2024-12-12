package com.example.playpal_livesearch_service.clients;


import com.example.playpal_livesearch_service.dto.GroupDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GroupClient {

    private final RestTemplate restTemplate;
    private final String groupServiceUrl;

    public GroupClient(RestTemplateBuilder builder, @Value("${group.service.url}")  String groupServiceUrl) {
        this.restTemplate = builder.build();
        this.groupServiceUrl = groupServiceUrl;
    }


    public Long createGroup(List<Long> userIds, String game, List<String> preferences) {
        String url = groupServiceUrl + "/group/api/group/new";

        log.info("Creating group for users: {}, game: {}, preferences: {}", userIds, game, preferences);

        Map<String, Object> groupRequest = Map.of(
                "userIds", userIds,
                "groupName", game + " Group",
                "groupDescription", String.join(", ", preferences) //
        );

        try {
            // Call Group Service endpoint and parse the response
            ResponseEntity<GroupDTO> response = restTemplate.postForEntity(url, groupRequest, GroupDTO.class);

            if (response.getBody() != null) {
                log.info("Group created successfully: {}", response.getBody());
                return response.getBody().getGroupID();
            }
        } catch (Exception e) {
            log.error("Failed to create group for users {}: {}", userIds, e.getMessage());
        }

        throw new RuntimeException("Failed to create group");
    }

}

