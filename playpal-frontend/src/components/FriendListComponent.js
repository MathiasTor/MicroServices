import React, { useState, useEffect } from "react";
import cookies from "js-cookie";
import axios from "axios";
import "./FriendsList.css";

const FriendsListComponent = () => {
    const [username, setUsername] = useState(cookies.get("username"));
    const [userId, setUserId] = useState(cookies.get("userid"));
    const [friends, setFriends] = useState([]);
    const [pendingRequests, setPendingRequests] = useState([]);
    const [blockedUsers, setBlockedUsers] = useState([]);
    const [newFriendUsername, setNewFriendUsername] = useState("");
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [activeTab, setActiveTab] = useState("friends"); // Tracks the active tab
    const [showAddFriend, setShowAddFriend] = useState(false); // Tracks visibility of Add Friend section

    const friendsAPI = "http://localhost:8080/friend/api/friends";
    const userAPI = "http://localhost:8080/user/api/users";

    useEffect(() => {
        if (username) {
            fetchFriends();
            fetchPendingRequests();
            fetchBlockedUsers();
        }
    }, [username]);

    const resolveUsernameById = async (id) => {
        try {
            const response = await axios.get(`${userAPI}/specific/${id}`);
            return response.data.username;
        } catch (error) {
            return `User (${id})`;
        }
    };

    const resolveIdsToUsernames = async (ids) => {
        const resolved = await Promise.all(ids.map((id) => resolveUsernameById(id)));
        return resolved;
    };

    const fetchFriends = async () => {
        try {
            const response = await axios.get(`${friendsAPI}/get-friends/${userId}`);
            const resolved = await resolveIdsToUsernames(response.data);
            setFriends(resolved);
        } catch (error) {
            setError("Failed to fetch friends list.");
        }
    };

    const fetchPendingRequests = async () => {
        try {
            const response = await axios.get(`${friendsAPI}/get-incoming-requests/${userId}`);
            const resolved = await resolveIdsToUsernames(response.data);
            setPendingRequests(resolved);
        } catch (error) {
            setError("Failed to fetch pending requests.");
        }
    };

    const fetchBlockedUsers = async () => {
        try {
            const response = await axios.get(`${friendsAPI}/get-blocked/${userId}`);
            const resolved = await resolveIdsToUsernames(response.data);
            setBlockedUsers(resolved);
        } catch (error) {
            setError("Failed to fetch blocked users.");
        }
    };

    const sendFriendRequest = async () => {
        if (!newFriendUsername) {
            setError("Please enter a username.");
            return;
        }

        try {
            const response = await axios.get(`${userAPI}/search?username=${newFriendUsername}`);
            const friendId = response.data.id;

            await axios.post(`${friendsAPI}/send-request/${userId}/${friendId}`);
            setSuccess(`Friend request sent to ${newFriendUsername}`);
            setNewFriendUsername("");
            setShowAddFriend(false); // Hide the Add Friend section after adding
        } catch (error) {
            setError("Failed to send friend request. Make sure the username exists.");
        }
    };

    const acceptFriendRequest = async (requestUsername) => {
        try {
            const response = await axios.get(`${userAPI}/search?username=${requestUsername}`);
            const friendId = response.data.id;

            await axios.post(`${friendsAPI}/accept-friend-request/${userId}/${friendId}`);
            setSuccess(`${requestUsername} has been added to your friends list.`);
            fetchPendingRequests(); // Refresh pending requests list
            fetchFriends(); // Refresh friends list
        } catch (error) {
            setError("Failed to accept friend request.");
        }
    };

    const removeFriend = async (friendUsername) => {
        if (window.confirm(`Are you sure you want to remove ${friendUsername} from your friends list?`)) {
            try {
                const response = await axios.get(`${userAPI}/search?username=${friendUsername}`);
                const friendId = response.data.id;

                await axios.post(`${friendsAPI}/remove-friend/${userId}/${friendId}`);
                setSuccess(`${friendUsername} has been removed from your friends list.`);
                fetchFriends(); // Refresh friends list
            } catch (error) {
                setError("Failed to remove friend.");
            }
        }
    };

    const renderTabContent = () => {
        switch (activeTab) {
            case "friends":
                return (
                    <div className="section">
                        <h3>Your Friends</h3>
                        {friends.length > 0 ? (
                            <ul>
                                {friends.map((friend, index) => (
                                    <li key={index}>
                                        {friend}{" "}
                                        <button
                                            onClick={() => removeFriend(friend)}
                                            className="remove-friend-button"
                                        >
                                            Remove
                                        </button>
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p>No friends found.</p>
                        )}
                    </div>
                );
            case "pending":
                return (
                    <div className="section">
                        <h3>Pending Requests</h3>
                        {pendingRequests.length > 0 ? (
                            <ul>
                                {pendingRequests.map((request, index) => (
                                    <li key={index}>
                                        {request}{" "}
                                        <button
                                            onClick={() => acceptFriendRequest(request)}
                                            className="accept-request-button"
                                        >
                                            Accept
                                        </button>
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p>No pending requests.</p>
                        )}
                    </div>
                );
            case "blocked":
                return (
                    <div className="section">
                        <h3>Blocked Users</h3>
                        {blockedUsers.length > 0 ? (
                            <ul>
                                {blockedUsers.map((blocked, index) => (
                                    <li key={index}>{blocked}</li>
                                ))}
                            </ul>
                        ) : (
                            <p>No blocked users.</p>
                        )}
                    </div>
                );
            default:
                return null;
        }
    };

    return (
        <div className="friends-list-container">
            <h2>Friends List</h2>
            {error && <p className="error-message">{error}</p>}
            {success && <p className="success-message">{success}</p>}

            <div className="add-friend-section">
                {!showAddFriend && (
                    <button className="add-friend-toggle" onClick={() => setShowAddFriend(true)}>
                        +
                    </button>
                )}
                {showAddFriend && (
                    <div className="add-friend-form">
                        <input
                            type="text"
                            placeholder="Enter username"
                            value={newFriendUsername}
                            onChange={(e) => setNewFriendUsername(e.target.value)}
                        />
                        <button onClick={sendFriendRequest}>Add Friend</button>
                        <button className="cancel-button" onClick={() => setShowAddFriend(false)}>
                            Cancel
                        </button>
                    </div>
                )}
            </div>

            {/* Tabs */}
            <div className="tabs">
                <button onClick={() => setActiveTab("friends")} className={activeTab === "friends" ? "active-tab" : ""}>
                    Friends
                </button>
                <button onClick={() => setActiveTab("pending")} className={activeTab === "pending" ? "active-tab" : ""}>
                    Pending {pendingRequests.length > 0 && <span className="notification-badge">{pendingRequests.length}</span>}
                </button>
                <button onClick={() => setActiveTab("blocked")} className={activeTab === "blocked" ? "active-tab" : ""}>
                    Blocked
                </button>
            </div>

            {/* Tab Content */}
            <div className="tab-content">{renderTabContent()}</div>
        </div>
    );
};

export default FriendsListComponent;
