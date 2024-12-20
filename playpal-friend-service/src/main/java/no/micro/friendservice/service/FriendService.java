package no.micro.friendservice.service;

import lombok.extern.slf4j.Slf4j;
import no.micro.friendservice.model.Friends;
import no.micro.friendservice.repo.FriendsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
public class FriendService {
    @Autowired
    private FriendsRepo friendsRepo;

    // Send friend request
    public Friends sendFriendRequest(Long senderId, Long recipientId) {
        Friends friendToAdd = friendsRepo.findByUserId(recipientId);
        Friends sender = friendsRepo.findByUserId(senderId);

        if(senderId.equals(recipientId)){
            log.info("User " + senderId + " tried to send a friend request to themselves");
            return null;
        }

        if(friendToAdd == null){
            friendToAdd = new Friends();
            friendToAdd.setUserId(recipientId);
            friendToAdd.setPendingIds(new ArrayList<>());
            friendToAdd.setFriendIds(new ArrayList<>());
            friendToAdd.setBlockedIds(new ArrayList<>());

            friendsRepo.save(friendToAdd);
        }

        if(sender == null){
            sender = new Friends();
            sender.setUserId(senderId);
            sender.setPendingIds(new ArrayList<>());
            sender.setFriendIds(new ArrayList<>());
            sender.setBlockedIds(new ArrayList<>());

            friendsRepo.save(sender);
        }

        //If blocked
        if(friendToAdd.getBlockedIds().contains(senderId)){
            log.info("User " + recipientId + " has blocked user " + senderId);
            return null;
        }

        sender.getPendingIds().add(recipientId);
        return friendsRepo.save(sender);
    }


    // Add friend
    public Friends addFriend(Long userId, Long friendId) {
        Friends friends = friendsRepo.findByUserId(userId);
        if (friends == null) {
            friends = new Friends();
            friends.setUserId(userId);
            friends.setPendingIds(new ArrayList<>());
            friends.setFriendIds(new ArrayList<>());
            friends.setBlockedIds(new ArrayList<>());
        }

        if (friends.getFriendIds() == null) {
            friends.setFriendIds(new ArrayList<>());
        }

        if (!friends.getFriendIds().contains(friendId)) {
            friends.getFriendIds().add(friendId);
            removeFriend(userId, friendId);
        }

        return friendsRepo.save(friends);
    }

    // Remove friend
    public Friends removeFriend(Long userId, Long friendId) {
        Friends friends = friendsRepo.findByUserId(userId);
        Friends friendToRemove = friendsRepo.findByUserId(friendId);

        if (friends != null && friends.getFriendIds() != null) {
            friends.getFriendIds().remove(friendId);
            log.info("Removed friend " + friendId + " from user " + userId);
        }

        if (friendToRemove != null && friendToRemove.getFriendIds() != null) {
            friendToRemove.getFriendIds().remove(userId);
            log.info("Removed friend " + userId + " from user " + friendId);
        }

        friendsRepo.save(friendToRemove);
        friendsRepo.save(friends);

        return friends;
    }

    // Block friend
    public Friends blockFriend(Long userId, Long friendId) {

        Friends user = friendsRepo.findByUserId(userId);
        Friends friend = friendsRepo.findByUserId(friendId);

        friend.getFriendIds().remove(userId);

        if (user == null) {
            user = new Friends();
            user.setUserId(userId);
            user.setPendingIds(new ArrayList<>());
            user.setFriendIds(new ArrayList<>());
            user.setBlockedIds(new ArrayList<>());
        }

        if (user.getBlockedIds() == null) {
            user.setBlockedIds(new ArrayList<>());
        }

        if (!user.getBlockedIds().contains(friendId)) {
            user.getBlockedIds().add(friendId);
            user.getFriendIds().remove(friendId);
        }

        friendsRepo.save(friend);
        return friendsRepo.save(user);
    }

    // Unblock friend
    public Friends unblockFriend(Long userId, Long friendId) {
        Friends friends = friendsRepo.findByUserId(userId);
        if (friends != null && friends.getBlockedIds() != null) {
            friends.getBlockedIds().remove(friendId);
            return friendsRepo.save(friends);
        }
        return friends;
    }

    // Get friends for user
    public List<Long> getFriendsForUser(Long userId) {
        Friends user = friendsRepo.findByUserId(userId);
        List<Long> friends = user != null ? new ArrayList<>(user.getFriendIds()) : new ArrayList<>();

        Iterator<Long> iterator = friends.iterator();
        while (iterator.hasNext()) {
            Long friendId = iterator.next();
            Friends friend = friendsRepo.findByUserId(friendId);
            if (friend != null && friend.getBlockedIds().contains(userId)) {
                iterator.remove();
            }
        }

        return friends;
    }


        // Get pending friend requests
    public List<Long> getPendingRequests(Long userId) {
        Friends friends = friendsRepo.findByUserId(userId);
        return friends != null ? friends.getPendingIds() : new ArrayList<>();
    }

    // Get blocked friends
    public List<Long> getBlocked(Long userId) {
        Friends friends = friendsRepo.findByUserId(userId);
        return friends != null ? friends.getBlockedIds() : new ArrayList<>();
    }

    public List<Long> getIncomingRequests(Long userId) {
        // Find all users whose `pendingIds` list contains the given `userId`
        List<Friends> friends = friendsRepo.findAll();
        List<Long> incomingRequests = new ArrayList<>();
        for (Friends friend : friends) {
            if (friend.getPendingIds() != null && friend.getPendingIds().contains(userId)) {
                incomingRequests.add(friend.getUserId());
            }
        }
        return incomingRequests;
    }

    @Value("${api.url}")
    private String apiUrl;

    public void acceptFriendRequest(Long userId, Long friendId) {
        Friends user = friendsRepo.findByUserId(userId);
        Friends friend = friendsRepo.findByUserId(friendId);

        if (user != null && friend != null) {
            user.getFriendIds().add(friendId);
            friend.getFriendIds().add(userId);

            friend.getPendingIds().remove(userId);

            // Create a conversation between the two users
            createConversation(userId, friendId);

            friendsRepo.save(friend);
            friendsRepo.save(user);
        }
    }

    private void createConversation(Long userId, Long friendId) {
        try {
            List<Long> userIds = new ArrayList<>();
            userIds.add(userId);
            userIds.add(friendId);

            String url = apiUrl + "/communication/api/conversations/dm";
            RestTemplate restTemplate = new RestTemplate();

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<List<Long>> request = new HttpEntity<>(userIds, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully created conversation between users " + userId + " and " + friendId);
            } else {
                log.error("Failed to create conversation between users " + userId + " and " + friendId);
            }
        } catch (Exception e) {
            log.error("Failed to create conversation between users " + userId + " and " + friendId);
        }
    }
}
