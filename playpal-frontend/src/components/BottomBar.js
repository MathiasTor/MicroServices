import React, { useState, useEffect } from "react";
import FriendsListComponent from "./FriendListComponent";
import axios from "axios";
import "./BottomBar.css";
import cookies from "js-cookie";

const BottomBar = () => {
    const [isFriendsVisible, setIsFriendsVisible] = useState(false);
    const [incomingRequests, setIncomingRequests] = useState(0); // Track the number of incoming requests
    const userId = cookies.get("userid");

    const friendsAPI = "http://localhost:8080/friend/api/friends";

    useEffect(() => {
        fetchIncomingRequests();
    }, []);

    const fetchIncomingRequests = async () => {
        try {
            const response = await axios.get(`${friendsAPI}/get-incoming-requests/${userId}`);
            if (Array.isArray(response.data)) {
                setIncomingRequests(response.data.length); // Only count actual incoming requests
            } else {
                setIncomingRequests(0); // Default to 0 if response is unexpected
            }
        } catch (error) {
            console.error("Failed to fetch incoming requests", error);
            setIncomingRequests(0); // Handle errors gracefully by setting to 0
        }
    };

    const toggleFriendsVisibility = () => {
        setIsFriendsVisible(!isFriendsVisible);
    };

    return (
        <div className="bottom-bar">
            <button className="friends-toggle-button" onClick={toggleFriendsVisibility}>
                Friends {incomingRequests > 0 && <span className="notification-badge">{incomingRequests}</span>}
            </button>
            {isFriendsVisible && (
                <div className="friends-list-container">
                    <FriendsListComponent onRequestAccepted={fetchIncomingRequests} />
                </div>
            )}
        </div>
    );
};

export default BottomBar;
