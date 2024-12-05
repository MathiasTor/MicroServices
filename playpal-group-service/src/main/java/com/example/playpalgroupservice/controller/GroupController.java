package com.example.playpalgroupservice.controller;

import com.example.playpalgroupservice.model.Group;
import com.example.playpalgroupservice.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/group")
public class GroupController {

    private final GroupService groupService;

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/all")
    public List<Group> getGroups(){
        logger.info("Fetching all groups");
        return groupService.getGroups();
    }

    @GetMapping("/id/{id}")
    public Group getGroupById(@PathVariable Long id){
        return groupService.getGroupById(id);
    }

    @PostMapping("/new")
    public Group createGroup(@RequestBody Group group){
        logger.info("Group created: " + group.getGroupName());
        return groupService.createGroup(group);
    }

    @DeleteMapping("delete/{id}")
    public void deleteGroupById(@PathVariable Long id){
        logger.info("Group deleted: " + id);
        groupService.deleteGroupById(id);
    }

    @PutMapping("/update")
    public Group updateGroup(@RequestBody Group group){
        logger.info("Group updated: " + group.getGroupName());
        return groupService.updateGroup(group);
    }

    @GetMapping("/{groupId}/userIds")
    public List<Long> getUserIdsForGroup(@PathVariable Long groupId) {
        return groupService.getUserIdsForGroup(groupId);
    }

    @PostMapping("/{groupId}/addUser/{userId}")
    public void addUserToGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        groupService.addUserToGroup(groupId, userId);
    }

    @DeleteMapping("/{groupId}/removeUser/{userId}")
    public void removeUserFromGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        groupService.removeUserFromGroup(groupId, userId);
    }

    @GetMapping("/getUserId/{username}")
    public Long getUserIdByUsername(@PathVariable String username) {
        return groupService.getUserIdByUsername(username);
    }

}
