import React, { useState, useEffect } from "react";
import FriendsListComponent from "./FriendListComponent";
import axios from "axios";
import "./BottomBar.css";
import cookies from "js-cookie";

const BottomBar = () => {
    const [isFriendsVisible, setIsFriendsVisible] = useState(false);
    const [incomingRequests, setIncomingRequests] = useState(0);
    const userId = cookies.get("userid");

    const friendsAPI = "http://localhost:8080/friend/api/friends";

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

    const toggleFriendsVisibility = () => {
        setIsFriendsVisible(!isFriendsVisible);
    };

    return (
        <div className="bottom-bar">
            <button className="chat-toggle-button">Chat</button>
            <button className="friends-toggle-button" onClick={toggleFriendsVisibility}>
                Friends {incomingRequests > 0 && <span className="notification-badge">{incomingRequests}</span>}
            </button>
            <button className="group-toggle-button">Groups</button>
            {isFriendsVisible && (
                <div className="friends-list-container">
                    <FriendsListComponent onRequestAccepted={fetchIncomingRequests} />
                </div>
            )}
        </div>
    );
};

export default BottomBar;
