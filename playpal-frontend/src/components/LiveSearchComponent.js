import React, { useState, useEffect } from "react";
import axios from "axios";
import Cookies from "js-cookie";
import "./LiveSearchComponent.css";

const LiveSearchComponent = () => {
    const [liveUsers, setLiveUsers] = useState([]);
    const [isLive, setIsLive] = useState(false);
    const [userId] = useState(Cookies.get("userid"));

    // Fetch live users
    useEffect(() => {
        const fetchLiveUsers = async () => {
            try {
                const response = await axios.get("http://localhost:8080/live-search/api/live-search/live-users");
                setLiveUsers(response.data);
            } catch (error) {
                console.error("Error fetching live users:", error);
            }
        };

        fetchLiveUsers();
    }, [isLive]);

    // Toggle live status
    const toggleLiveStatus = async () => {
        try {
            if (isLive) {
                // Stop live
                await axios.post(`http://localhost:8080/api/live-search/stop-live/${userId}`);
                setIsLive(false);
            } else {
                // Go live
                await axios.post(`http://localhost:8080/api/live-search/go-live/${userId}`, {
                    preferences: [], // Empty for now, can be expanded
                    videoGame: "General", // Default or dynamic game type
                });
                setIsLive(true);
            }
        } catch (error) {
            console.error("Error toggling live status:", error);
        }
    };

    return (
        <div className="live-search-container">
            <h1>Live Matchmaking</h1>

            {/* Toggle Live Button */}
            <button
                className={`toggle-live-button ${isLive ? "active" : ""}`}
                onClick={toggleLiveStatus}
            >
                {isLive ? "You Are Live" : "Go Live"}
            </button>

            {/* Live Users List */}
            <h3>Currently Searching Players</h3>
            <div className="live-users-list">
                {liveUsers.length > 0 ? (
                    liveUsers.map((user) => (
                        <div
                            key={user.userId}
                            className={`live-user-card ${userId === user.userId ? "self" : ""}`}
                        >
                            <h4>User ID: {user.userId}</h4>
                            <p>Video Game: {user.videoGame}</p>
                        </div>
                    ))
                ) : (
                    <p>No live users found.</p>
                )}
            </div>
        </div>
    );
};

export default LiveSearchComponent;
