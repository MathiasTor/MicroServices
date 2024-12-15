import React, { useState, useEffect } from 'react';
import axios from 'axios';
import cookies from 'js-cookie';
import { useNavigate } from 'react-router-dom';
import './LiveSearch.css';

const LiveSearch = () => {
    const userId = cookies.get('userid');
    const navigate = useNavigate();

    const [liveUsers, setLiveUsers] = useState([]);
    const [isLive, setIsLive] = useState(false);
    const [loading, setLoading] = useState(false);
    const [matchMessage, setMatchMessage] = useState('');
    const [liveUserSearchId, setLiveUserSearchId] = useState(null);
    const [groupInfo, setGroupInfo] = useState(null);

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                // Fetch all live users
                const usersResponse = await axios.get('http://localhost:8080/livesearch/api/live/all');
                const usersData = usersResponse.data;

                // Filter active users
                const activeUsers = usersData.filter(user => user.active === 1);
                setLiveUsers(activeUsers);

                // Fetch user status (returns true/false)
                const statusResponse = await axios.get(`http://localhost:8080/livesearch/api/live/status/${userId}`);
                const statusData = statusResponse.data;

                // Set isLive based on the status endpoint
                setIsLive(statusData);
            } catch (error) {
                console.error('Error fetching live users or status:', error);
            }
        };

        fetchUsers();
        const interval = setInterval(fetchUsers, 5000);
        return () => clearInterval(interval);
    }, [userId]);

    useEffect(() => {
        if (!liveUserSearchId) return;

        const pollMatchStatus = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/livesearch/api/live/match-status/${liveUserSearchId}`);
                const isMatched = response.data;

                if (isMatched) {
                    setMatchMessage('Match found! You are successfully matched.');
                    clearInterval(pollInterval); // Stop polling if a match is found
                    fetchGroupInfo();
                }
            } catch (error) {
                console.error('Error polling match status:', error);
            }
        };

        const pollInterval = setInterval(pollMatchStatus, 5000);
        return () => clearInterval(pollInterval);
    }, [liveUserSearchId]);

    const fetchGroupInfo = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/group/api/group/latest/${userId}`);
            setGroupInfo(response.data);
        } catch (error) {
            console.error('Error fetching group info:', error);
        }
    };

    const handleGroupRedirect = () => {
        if (groupInfo) {
            navigate(`/groups/${groupInfo.groupID}`);
        }
    };

    const goLive = async () => {
        setLoading(true);
        try {
            const response = await axios.post(`http://localhost:8080/livesearch/api/live/new/${userId}`);
            if (response.status === 200 || response.status === 201) {
                const data = response.data;

                // Save the LiveUserSearch ID for polling
                setLiveUserSearchId(data.id);

                // Set a message based on initial response
                if (data.userId !== 0 && data.matchedUserId !== 0) {
                    setMatchMessage(`Match found immediately! You are matched with User ID: ${data.matchedUserId}`);
                    fetchGroupInfo();
                } else {
                    setMatchMessage('You are now live and waiting for a match...');
                }

                // Update the live state
                setIsLive(true);
            } else {
                throw new Error('Failed to go live');
            }
        } catch (err) {
            console.error(err);
            setMatchMessage('An error occurred while going live.');
        } finally {
            setLoading(false);
        }
    };

    const stopLive = async () => {
        setLoading(true);
        try {
            const response = await axios.put(`http://localhost:8080/livesearch/api/live/stop/${userId}`);
            if (response.status === 200) {
                setMatchMessage('You have stopped being live.');
                setIsLive(false);
                setLiveUserSearchId(null);
            } else {
                throw new Error('Failed to stop live');
            }
        } catch (err) {
            console.error(err);
            setMatchMessage('An error occurred while stopping live.');
        } finally {
            setLoading(false);
        }
    };

    const toggleLive = async () => {
        if (isLive) {
            await stopLive();
        } else {
            await goLive();
        }
    };

    return (
        <div className="live-search-container">
            <h1>Live Search / Matchmaking</h1>
            {matchMessage && (
                <div className="match-message">
                    {matchMessage}
                </div>
            )}
            {groupInfo && (
                <div className="group-info-box">
                    <button onClick={handleGroupRedirect}>Go to Group Overview</button>
                    <p>Group Name: {groupInfo.groupName}</p>
                </div>
            )}
            <button
                onClick={toggleLive}
                disabled={loading}
                className={`toggle-button ${isLive ? 'live' : ''}`}
            >
                {isLive ? 'Stop Live' : 'Go Live'}
            </button>

            <h2>All Live Users</h2>
            {liveUsers.length === 0 ? (
                <p className="no-users">No one is live right now.</p>
            ) : (
                <ul className="user-list">
                    {liveUsers.map(user => (
                        <li key={user.id} className="user-card">
                            <div className="user-info">
                                <strong>User ID:</strong> {user.userId}
                            </div>
                            <div className="user-info">
                                <strong>Matched User ID:</strong> {user.matchedUserId !== 0 ? user.matchedUserId : 'None'}
                            </div>
                            <div className="user-info">
                                <strong>Active:</strong> {user.active === 1 ? 'Yes' : 'No'}
                            </div>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default LiveSearch;
