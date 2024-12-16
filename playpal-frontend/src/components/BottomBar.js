import React, { useState, useEffect } from "react";
import FriendsListComponent from "./FriendListComponent";
import axios from "axios";
import "./BottomBar.css";
import cookies from "js-cookie";
import { useNavigate } from "react-router-dom";

const BottomBar = () => {
    const [isFriendsVisible, setIsFriendsVisible] = useState(false);
    const [isChatVisible, setIsChatVisible] = useState(false);
    const [dmList, setDmList] = useState([]);
    const [usernames, setUsernames] = useState({});
    const [loading, setLoading] = useState(false);
    const [incomingRequests, setIncomingRequests] = useState(0);
    const [newMessages, setNewMessages] = useState([]);
    const userId = cookies.get("userid");
    const navigate = useNavigate();

    const friendsAPI = "http://localhost:8080/friend/api/friends";
    const dmAPI = `http://localhost:8080/communication/api/conversations/dm/${userId}`;
    const userAPI = "http://localhost:8080/user/api/users/specific";

    useEffect(() => {
        fetchIncomingRequests();
    }, []);

    const fetchIncomingRequests = async () => {
        try {
            const response = await axios.get(`${friendsAPI}/get-incoming-requests/${userId}`);
            if (Array.isArray(response.data)) {
                setIncomingRequests(response.data.length);
            } else {
                setIncomingRequests(0);
            }
        } catch (error) {
            console.error("Failed to fetch incoming requests", error);
            setIncomingRequests(0);
        }
    };

    const fetchUsernames = async (userIds) => {
        const usernameMap = { ...usernames };
        const fetchPromises = userIds
            .filter((id) => !usernameMap[id])
            .map(async (id) => {
                try {
                    const response = await axios.get(`${userAPI}/${id}`);
                    return { id, username: response.data.username };
                } catch (error) {
                    console.error(`Failed to fetch username for user ID ${id}`, error);
                    return { id, username: `User ${id}` };
                }
            });

        const results = await Promise.all(fetchPromises);
        results.forEach(({ id, username }) => {
            usernameMap[id] = username;
        });

        setUsernames(usernameMap);
    };

    const toggleFriendsVisibility = () => {
        setIsFriendsVisible((prev) => {
            if (!prev) setIsChatVisible(false);
            return !prev;
        });
    };

    const toggleChatVisibility = async () => {
        setIsChatVisible((prev) => {
            if (!prev) setIsFriendsVisible(false);
            return !prev;
        });

        if (!isChatVisible) {
            setLoading(true);
            try {
                // Fetch the list of DMs
                const response = await axios.get(dmAPI);
                const allDms = response.data;

                // Fetch blocked users and normalize IDs
                const blockedResponse = await axios.get(`${friendsAPI}/get-blocked/${userId}`);
                const blockedUsers = new Set(blockedResponse.data.map(String));

                console.log("Blocked Users: ", blockedUsers);

                // Filter out DMs with blocked users
                const filteredDms = allDms.filter((dm) =>
                    dm.userIds.every((id) => String(id) === String(userId) || !blockedUsers.has(String(id)))
                );

                console.log("Filtered DMs: ", filteredDms);
                setDmList(filteredDms);

                // Fetch usernames for remaining recipient IDs
                const allRecipientIds = filteredDms.flatMap((dm) =>
                    dm.userIds.filter((id) => String(id) !== String(userId))
                );
                await fetchUsernames(allRecipientIds);
            } catch (error) {
                console.error("Failed to fetch DM list", error);
                setDmList([]);
            } finally {
                setLoading(false);
            }
        }
    };

    const handleDmSelection = async (recipientId) => {
        try {
            const response = await axios.get(`${dmAPI}/${recipientId}`);
            const conversationId = response.data;
            navigate(`/dm/${conversationId}`);
        } catch (error) {
            console.error("Failed to fetch DM conversation ID", error);
        }
    };

    const newMessage = () => {
        setNewMessages((prev) => [...prev, ""]);
    };

    const handleUsernameSubmit = async (username, index) => {
        if (username === "") {
            console.error("Username is empty");
            return;
        }

        try {
            const userResponse = await axios.get(`http://localhost:8080/user/api/users/get-id/${username}`);
            const userIdToadd = userResponse.data;

            console.log("User ID: ", userIdToadd);

            if (!userIdToadd) {
                console.error("User not found");
                return;
            }

            const self = parseInt(userId);;

            createConversation(self, userIdToadd);

        } catch (error) {
            console.error("Failed to fetch user ID", error);
        }
    };

    const createConversation = async (user1, user2) => {
        try {
            const communicationResponse = await axios.get(`http://localhost:8080/communication/api/conversations/create/${user1}/${user2}`);
            console.log("Conversation Created:", communicationResponse.data);
            toggleChatVisibility();
        } catch (error) {
            console.error("Failed to create conversation", error);
        }
    };

    const handleInputChange = (value, index) => {
        setNewMessages((prev) => {
            const updated = [...prev];
            updated[index] = value;
            return updated;
        });
    };

    return (
        <div className="bottom-bar">
            <button className="chat-toggle-button" onClick={toggleChatVisibility}>
                Chat
            </button>
            <button className="friends-toggle-button" onClick={toggleFriendsVisibility}>
                Friends {incomingRequests > 0 && <span className="notification-badge">{incomingRequests}</span>}
            </button>

            {isChatVisible && (
                <div className="chat-list-container">
                    <button onClick={newMessage}>+</button>
                    <h3>Your DMs</h3>
                    {newMessages.map((message, index) => (
                        <div key={index} className="new-message-container">
                            <input
                                type="text"
                                placeholder="Username"
                                value={newMessages[index]}
                                onChange={(e) => handleInputChange(e.target.value, index)}
                                className="new-message-input"
                            />
                            <button onClick={() => handleUsernameSubmit(newMessages[index], index)}>
                                Submit
                            </button>
                        </div>
                    ))}
                    {loading ? (
                        <p>Loading...</p>
                    ) : (
                        <ul>
                            {dmList.map((dm) =>
                                dm.userIds
                                    .filter((id) => String(id) !== String(userId))
                                    .map((recipientId) => (
                                        <li key={recipientId}>
                                            <button
                                                onClick={() => handleDmSelection(recipientId)}
                                                className="dm-recipient-button"
                                            >
                                                {usernames[recipientId] || `User ${recipientId}`}
                                            </button>
                                        </li>
                                    ))
                            )}
                        </ul>
                    )}
                </div>
            )}

            {isFriendsVisible && (
                <div className="friends-list-container">
                    <FriendsListComponent onRequestAccepted={fetchIncomingRequests} />
                </div>
            )}
        </div>
    );
};

export default BottomBar;
