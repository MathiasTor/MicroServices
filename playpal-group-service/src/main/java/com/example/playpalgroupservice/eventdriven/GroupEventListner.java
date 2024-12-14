package com.example.playpalgroupservice.eventdriven;

import com.example.playpalgroupservice.dto.SearchEndedEvent;
import com.example.playpalgroupservice.model.Group;
import com.example.playpalgroupservice.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupEventListner {

    private final GroupService groupService;

    @RabbitListener(queues = "${amqp.queue.stopped.name}")
    public void handleSearchEndedEvent(SearchEndedEvent event) {
        log.info("Received search.ended event: {}", event);


        Group group = new Group();
        group.setGroupName(event.getTitle());
        group.setGroupDescription(event.getDescription());

        List<Long> approvedUsers = event.getApprovedUsers();
        approvedUsers.add(event.getUserId());

        group.setUserIds(approvedUsers);

        groupService.createGroup(group);
    }

}
