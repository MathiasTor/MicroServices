package no.micro.friendservice.service;

import no.micro.friendservice.model.Friends;
import no.micro.friendservice.repo.FriendsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FriendService {
    @Autowired
    private FriendsRepo friendsRepo;

    // Send friend request
    public Friends sendFriendRequest(Long senderId, Long recipientId) {
        Friends friendToAdd = friendsRepo.findByUserId(recipientId);
        Friends sender = friendsRepo.findByUserId(senderId);

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

        System.out.println("Request from: " + senderId + " to: " + recipientId);

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
        }

        return friendsRepo.save(friends);
    }

    // Remove friend
    public Friends removeFriend(Long userId, Long friendId) {
        Friends friends = friendsRepo.findByUserId(userId);
        if (friends != null && friends.getFriendIds() != null) {
            friends.getFriendIds().remove(friendId);
            return friendsRepo.save(friends);
        }
        return friends; // Return null or handle appropriately
    }

    // Block friend
    public Friends blockFriend(Long userId, Long friendId) {
        Friends friends = friendsRepo.findByUserId(userId);
        if (friends == null) {
            friends = new Friends();
            friends.setUserId(userId);
            friends.setPendingIds(new ArrayList<>());
            friends.setFriendIds(new ArrayList<>());
            friends.setBlockedIds(new ArrayList<>());
        }

        if (friends.getBlockedIds() == null) {
            friends.setBlockedIds(new ArrayList<>());
        }

        if (!friends.getBlockedIds().contains(friendId)) {
            friends.getBlockedIds().add(friendId);
        }

        return friendsRepo.save(friends);
    }

    // Unblock friend
    public Friends unblockFriend(Long userId, Long friendId) {
        Friends friends = friendsRepo.findByUserId(userId);
        if (friends != null && friends.getBlockedIds() != null) {
            friends.getBlockedIds().remove(friendId);
            return friendsRepo.save(friends);
        }
        return friends; // Return null or handle appropriately
    }

    // Get friends for user
    public List<Long> getFriendsForUser(Long userId) {
        Friends friends = friendsRepo.findByUserId(userId);
        return friends != null ? friends.getFriendIds() : new ArrayList<>();
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

    public void acceptFriendRequest(Long userId, Long friendId) {
        Friends user = friendsRepo.findByUserId(userId);
        Friends friend = friendsRepo.findByUserId(friendId);

        if(user != null && friend != null) {
            user.getFriendIds().add(friendId);
            friend.getFriendIds().add(userId);

            friend.getPendingIds().remove(userId);

            friendsRepo.save(friend);
            friendsRepo.save(user);
        }
    }
}
