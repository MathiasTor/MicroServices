import React, { useState, useEffect } from "react";
import axios from "axios";
import Cookies from "js-cookie";
import { useNavigate } from "react-router-dom";
import "./LiveSearchComponent.css";

const LiveSearchComponent = () => {
    const [liveUsers, setLiveUsers] = useState([]);
    const [isLive, setIsLive] = useState(false);
    const [loading, setLoading] = useState(false); // Add loading state
    const [matchedUser, setMatchedUser] = useState(null);
    const [userId] = useState(Cookies.get("userid"));
    const navigate = useNavigate();

    const fetchLiveStatus = async () => {
        try {
            const response = await axios.get(
                `http://localhost:8080/livesearch/api/live-search/status/${userId}`
            );
            setIsLive(response.data.isLive);
            if (response.data.matchedUser) {
                setMatchedUser(response.data.matchedUser);
            }
        } catch (error) {
            console.error("Error fetching live status:", error);
        }
    };

    useEffect(() => {
        fetchLiveStatus();
        const interval = setInterval(fetchLiveStatus, 1000);
        return () => clearInterval(interval);
    }, [userId]);

    const fetchLiveUsers = async () => {
        try {
            const response = await axios.get(
                "http://localhost:8080/livesearch/api/live-search/live-users"
            );
            const otherLiveUsers = response.data.filter(
                (user) => user.userId !== parseInt(userId)
            );
            setLiveUsers(otherLiveUsers);
        } catch (error) {
            console.error("Error fetching live users:", error);
        }
    };

    useEffect(() => {
        fetchLiveUsers();
        const interval = setInterval(fetchLiveUsers, 5000);
        return () => clearInterval(interval);
    }, [userId]);

    const pollForMatch = async (userId) => {
        let retries = 0;
        const maxRetries = 10;
        let isMatchFound = false; // Flag to stop unnecessary calls

        const interval = setInterval(async () => {
            if (isMatchFound) {
                clearInterval(interval);
                return;
            }

            try {
                const response = await axios.post(
                    `http://localhost:8080/livesearch/api/live-search/match-2/${userId}`
                );

                console.log("Match response:", response.data);

                if (response.status === 200 && response.data) {
                    clearInterval(interval);
                    isMatchFound = true; // Update flag
                    setMatchedUser(response.data);
                    setIsLive(false);

                    // Redirect to group route
                    handleGroupRedirect(userId);

                }
            } catch (error) {
                console.error("Error polling for match:", error);
                clearInterval(interval); // Clear interval on error
            }

            retries++;
            if (retries >= maxRetries) {
                clearInterval(interval);
            }
        }, 1000);
    };

    const handleGroupRedirect = async (userId) => {
        const response = await axios.get(`http://localhost:8080/group/api/group/latest/${userId}`);
        const groupId = response.data.groupID;

        navigate(`/groups/${groupId}`);
    }

    const toggleLiveStatus = async () => {
        if (loading) return;

        setLoading(true);
        try {
            if (isLive) {
                await axios.put(
                    `http://localhost:8080/livesearch/api/live-search/disable/${userId}`
                );
                setIsLive(false);

                let retries = 0;
                const maxRetries = 5;
                const interval = setInterval(async () => {
                    try {
                        const response = await axios.get(
                            `http://localhost:8080/livesearch/api/live-search/status/${userId}`
                        );
                        if (response.data.matchedUser) {
                            setMatchedUser(response.data.matchedUser);
                            clearInterval(interval);
                        }
                    } catch (error) {
                        console.error("Error checking for match after stopping live:", error);
                    }
                    retries++;
                    if (retries >= maxRetries) {
                        clearInterval(interval);
                    }
                }, 1000);
            } else {
                await axios.post(
                    `http://localhost:8080/livesearch/api/live-search/enable/${userId}`,
                    { tags: ["Ernad bro", "Lose my rating"], videoGame: "XD" }
                );
                setIsLive(true);
                pollForMatch(userId);
            }
        } catch (error) {
            console.error("Error toggling live status:", error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="live-search-container">
            <h1>Live Matchmaking</h1>
            <button
                className={`toggle-live-button ${isLive ? "active" : ""}`}
                onClick={toggleLiveStatus}
                disabled={loading}
            >
                {isLive ? "You Are Live" : "Go Live"}
            </button>
            {matchedUser && (
                <div className="match-notification">
                    <h2>Match Found!</h2>
                    <p>Matched with User ID: {matchedUser.userId}</p>
                    <p>Video Game: {matchedUser.videoGame}</p>
                </div>
            )}
            <h3>Currently Searching Players</h3>
            <div className="live-users-list">
                {liveUsers.length > 0 ? (
                    liveUsers.map((user) => (
                        <div
                            key={user.userId}
                            className={`live-user-card ${
                                userId === user.userId ? "self" : ""
                            }`}
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
