package com.example.playpalgroupservice.service;

import com.example.playpalgroupservice.model.Group;
import com.example.playpalgroupservice.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public List<Group> getGroups() {
        return groupRepository.findAll();
    }

    public Group getGroupById(Long id) {
        return groupRepository.findById(id).orElse(null);
    }

    public Group createGroup(Group group) {
        return groupRepository.save(group);
    }

    public void deleteGroupById(Long id) {
        groupRepository.deleteById(id);
    }

    public Group updateGroup(Group group) {
        return groupRepository.save(group);
    }


}
