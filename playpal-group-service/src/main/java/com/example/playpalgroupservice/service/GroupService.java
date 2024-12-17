package com.example.playpalgroupservice.service;

import com.example.playpalgroupservice.dto.GroupDTO;
import com.example.playpalgroupservice.dto.SearchEndedEvent;
import com.example.playpalgroupservice.eventdriven.GroupEventPublisher;
import com.example.playpalgroupservice.model.Group;
import com.example.playpalgroupservice.repository.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupEventPublisher groupEventPublisher; // Add this dependency

    @Value("${user.service.url}")
    private String userServiceUrl; // Base URL of User service (e.g., http://user-service)

    @Autowired
    public GroupService( GroupRepository groupRepository, GroupEventPublisher groupEventPublisher) {

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

        groupDTO.setGroupName(savedGroup.getGroupName());
        groupDTO.setUserIds(group.getUserIds());

        groupEventPublisher.publishGroupCreatedEvent(groupDTO);

        log.info("Name of the group: {}", group.getGroupName());
        log.info("Members of the group: {}", group.getUserIds());
        log.info("Group created: {}", savedGroup);
        log.info("GroupDTO created: {}", groupDTO);

        log.info("Publishing GroupDTO to RabbitMQ: {}", groupDTO);
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

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with ID: " + groupId));


        return group.getUserIds();
    }

    public List<Group> getGroupsForUser(Long userId) {
        return groupRepository.findAllByUserIdsContains(userId);
    }

    public Group getLatestGroupForUser(Long userId) {
        List<Group> groups = groupRepository.findAllByUserIdsContains(userId);
        return groups.isEmpty() ? null : groups.get(groups.size() - 1);
    }
}
