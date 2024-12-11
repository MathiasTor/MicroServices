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
            if (!prev) setIsChatVisible(false); // Close chat if friends section is opened
            return !prev;
        });
    };

    const toggleChatVisibility = async () => {
        setIsChatVisible((prev) => {
            if (!prev) setIsFriendsVisible(false); // Close friends if chat section is opened
            return !prev;
        });
        if (!isChatVisible) {
            setLoading(true);
            try {
                const response = await axios.get(dmAPI);
                setDmList(response.data);

                const allRecipientIds = response.data.flatMap((dm) =>
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
                    <h3>Your DMs</h3>
                    {loading ? (
                        <p>Loading...</p>
                    ) : (
                        <ul>
                            {dmList.map((dm) =>
                                dm.userIds
                                    .filter((id) => String(id) !== String(userId)) // Show only recipients
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
