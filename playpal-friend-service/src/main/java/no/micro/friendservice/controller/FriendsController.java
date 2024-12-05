package no.micro.friendservice.controller;

import no.micro.friendservice.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendsController {

    @Autowired
    private FriendService friendsService;

    @PostMapping("/send-request/{userId}/{friendId}")
    public void sendFriendRequest(@PathVariable Long userId, @PathVariable Long friendId) {
        friendsService.sendFriendRequest(userId, friendId);
    }

    @PostMapping("/add-friend/{userId}/{friendId}")
    public void addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        friendsService.addFriend(userId, friendId);
    }

    @PostMapping("/remove-friend/{userId}/{friendId}")
    public void removeFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        friendsService.removeFriend(userId, friendId);
    }

    @PostMapping("/block-friend/{userId}/{friendId}")
    public void blockFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        friendsService.blockFriend(userId, friendId);
    }

    @PostMapping("/unblock-friend/{userId}/{friendId}")
    public void unblockFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        friendsService.unblockFriend(userId, friendId);
    }

    //Get friends for user
    @GetMapping("/get-friends/{userId}")
    public List<Long> getFriendsForUser(@PathVariable Long userId) {
        return friendsService.getFriendsForUser(userId);
    }

    //Get pending friend requests
    @GetMapping("/get-pending-requests/{userId}")
    public List<Long> getPendingRequests(@PathVariable Long userId) {
        return friendsService.getPendingRequests(userId);
    }

    //Get incoming friend requests
    @GetMapping("/get-incoming-requests/{userId}")
    public List<Long> getIncomingRequests(@PathVariable Long userId) {
        return friendsService.getIncomingRequests(userId);
    }

    //Get blocked friends
    @GetMapping("/get-blocked/{userId}")
    public List<Long> getBlockedFriends(@PathVariable Long userId) {
        return friendsService.getBlocked(userId);
    }

    //accept friend request
    @PostMapping("/accept-friend-request/{userId}/{friendId}")
    public void acceptFriendRequest(@PathVariable Long userId, @PathVariable Long friendId) {
        friendsService.acceptFriendRequest(userId, friendId);
    }

}
