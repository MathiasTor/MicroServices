import React, { useState, useEffect } from 'react';
import axios from 'axios';
import cookies from 'js-cookie';
import { useNavigate } from 'react-router-dom';
import './LiveSearch.css';

const LiveSearch = () => {
    const userId = cookies.get('userid');
    const navigate = useNavigate();

    const [isLive, setIsLive] = useState(false);
    const [loading, setLoading] = useState(false);
    const [matchMessage, setMatchMessage] = useState('');
    const [groupInfo, setGroupInfo] = useState(null);



    const fetchLiveStatus = async () => {
        try {
            setGroupInfo("")
            const response = await axios.get(`http://localhost:8080/livesearch/api/live/status/${userId}`);
            setIsLive(response.data); // Assume the backend returns a boolean
        } catch (error) {
            console.error("Error fetching live status:", error);
        }
    };


    useEffect(() => {
        fetchLiveStatus(); // Check initial live status
        const interval = setInterval(fetchUnreadMatch, 5000); // Check for unread matches every 5 seconds
        return () => clearInterval(interval); // Cleanup on component unmount
    }, [userId]);



    const fetchUnreadMatch = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/livesearch/api/live/unread-match/${userId}`);

            // If successful, update the match message and fetch group info
            setMatchMessage(response.data); // Assuming the backend sends the match message
            fetchGroupInfo(); // Fetch additional group details
        } catch (error) {
            if (error.response && error.response.status === 404) {
                // No unread matches

            } else {
                // Handle other errors
                console.error("Error fetching unread match:", error);
            }
        }
    };


    const fetchGroupInfo = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/group/api/group/latest/${userId}`);
            setGroupInfo(response.data);
            setIsLive(false)
        } catch (error) {
            console.error('Error fetching group info:', error);
        }
    };


    const goLive = async () => {
        setLoading(true);
        try {
            setGroupInfo("")
            const response = await axios.post(`http://localhost:8080/livesearch/api/live/new/${userId}`);
            if (response.status === 200 || response.status === 201) {
                setIsLive(true);
                setMatchMessage('You are now live and waiting for a match...');
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
                setIsLive(false);
                setMatchMessage('You have stopped being live.');
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

    const handleGroupRedirect = () => {
        if (groupInfo) {
            navigate(`/groups/${groupInfo.groupID}`);
        }
    };

    useEffect(() => {
        if (matchMessage.includes("Match found")) {
            setIsLive(false); // Stop the live stream if a match is found
        }
    }, [matchMessage]);

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
        </div>
    );
};

export default LiveSearch;
