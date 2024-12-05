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

    //send friend request
    public Friends sendFriendRequest(Long userId, Long friendId) {
        Friends friends = friendsRepo.findByUserId(friendId);
        if (friends.getPendingIds() == null) {
            friends.setPendingIds(new ArrayList<>());
        }
        friends.getPendingIds().add(userId);
        return friendsRepo.save(friends);
    }

    //add friend
    public Friends addFriend(Long userId, Long friendId) {
        Friends friends = friendsRepo.findByUserId(userId);
        if (friends.getFriendIds() == null) {
            friends.setFriendIds(new ArrayList<>());
        }
        friends.getFriendIds().add(friendId);
        return friendsRepo.save(friends);
    }

    //remove friend
    public Friends removeFriend(Long userId, Long friendId) {
        Friends friends = friendsRepo.findByUserId(userId);
        friends.getFriendIds().remove(friendId);
        return friendsRepo.save(friends);
    }

    //block friend
    public Friends blockFriend(Long userId, Long friendId) {
        Friends friends = friendsRepo.findByUserId(userId);
        if (friends.getBlockedIds() == null) {
            friends.setBlockedIds(new ArrayList<>());
        }
        friends.getBlockedIds().add(friendId);
        return friendsRepo.save(friends);
    }

    //unblock friend
    public Friends unblockFriend(Long userId, Long friendId) {
        Friends friends = friendsRepo.findByUserId(userId);
        friends.getBlockedIds().remove(friendId);
        return friendsRepo.save(friends);
    }

    //get friends for user
    public List<Long> getFriendsForUser(Long userId) {
        Friends friends = friendsRepo.findByUserId(userId);
        return friends.getFriendIds();
    }

    //get pending friend requests
    public List<Long> getPendingRequests(Long userId) {
        Friends friends = friendsRepo.findByUserId(userId);
        return friends.getPendingIds();
    }

    //get blocked friends
    public List<Long> getBlocked(Long userId) {
        Friends friends = friendsRepo.findByUserId(userId);
        return friends.getBlockedIds();
    }
}
