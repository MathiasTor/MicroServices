package com.example.playpalgroupservice.service;

import com.example.playpalgroupservice.dto.GroupDTO;
import com.example.playpalgroupservice.dto.UserDTO;
import com.example.playpalgroupservice.eventdriven.GroupEventPublisher;
import com.example.playpalgroupservice.model.Group;
import com.example.playpalgroupservice.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class GroupService {

    private final RestTemplate restTemplate;
    private final GroupRepository groupRepository;
    private final GroupEventPublisher groupEventPublisher; // Add this dependency

    @Value("${user.service.url}")
    private String userServiceUrl; // Base URL of User service (e.g., http://user-service)

    @Autowired
    public GroupService(RestTemplate restTemplate, GroupRepository groupRepository, GroupEventPublisher groupEventPublisher) {
        this.restTemplate = restTemplate;
        this.groupRepository = groupRepository;
        this.groupEventPublisher = groupEventPublisher;
    }

    public List<Group> getGroups() {
        return groupRepository.findAll();
    }

    public Group getGroupById(Long id) {
        return groupRepository.findById(id).orElse(null);
    }

    public Group createGroup(Group group) {
        // Save the group to the database
        Group savedGroup = groupRepository.save(group);

        // Publish the group.created event
        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setGroupId(savedGroup.getGroupID());
        groupDTO.setUserIds(savedGroup.getUserIds());
        groupEventPublisher.publishGroupCreatedEvent(groupDTO);

        return savedGroup;
    }

    public void deleteGroupById(Long id) { groupRepository.deleteById(id);}

    public Group updateGroup(Group group) {
        return groupRepository.save(group);
    }


    public void addUserToGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group != null) {
            group.getUserIds().add(userId);
            groupRepository.save(group);
        }
    }

    public void removeUserFromGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group != null) {
            group.getUserIds().remove(userId);
            groupRepository.save(group);
        }
    }

    public List<Long> getUserIdsForGroup(Long groupId) {
        // Find the group by its ID
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with ID: " + groupId));


        return group.getUserIds();
    }

    public Long getUserIdByUsername(String username) {
        String url = userServiceUrl + "/users/" + username;
        ResponseEntity<UserDTO> response = restTemplate.getForEntity(url, UserDTO.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            UserDTO user = response.getBody();
            return user != null ? user.getUserId() : null;
        }

        throw new RuntimeException("Failed to fetch user ID for username: " + username);
    }


}
