import React, { useState, useEffect } from "react";
import "./GroupComponent.css";
import TopBar from "./TopBar";
import BottomBar from "./BottomBar";
import cookies from "js-cookie";
import axios from "axios";

const GroupComponent = () => {
    const [groups, setGroups] = useState([]);
    const [userId] = useState(cookies.get("userid"));
    const [usernames, setUsernames] = useState({});
    const [newGroupName, setNewGroupName] = useState("");
    const [newGroupDescription, setNewGroupDescription] = useState("");
    const [newGroupMembers, setNewGroupMembers] = useState("");

    const fetchGroups = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/group/api/group/user/${userId}`);
            setGroups(response.data);
        } catch (error) {
            console.error("Error fetching groups:", error);
        }
    };

    const fetchUsernames = async (userIds) => {
        try {
            const usernameMap = { ...usernames };
            const fetchPromises = userIds
                .filter((id) => !usernameMap[id])
                .map(async (id) => {
                    const response = await axios.get(`http://localhost:8080/user/api/users/specific/${id}`);
                    return { id, username: response.data.username };
                });

            const results = await Promise.all(fetchPromises);
            results.forEach(({ id, username }) => {
                usernameMap[id] = username;
            });

            setUsernames(usernameMap);
        } catch (error) {
            console.error("Error fetching usernames:", error);
        }
    };

    const groupNavigation = (groupID) => {
        window.location.href = `/groups/${groupID}`;
    };

    const handleCreateGroup = async () => {
        try {
            const memberNames = newGroupMembers
                .split(",")
                .map((name) => name.trim())
                .filter((name) => name.length > 0);

            const userIdPromises = memberNames.map(async (username) => {
                const response = await axios.get(`http://localhost:8080/user/api/users/get-id/${username}`);
                return response.data;
            });

            const resolvedUserIds = await Promise.all(userIdPromises);

            // Ensure the creator is also added to the group
            if (!resolvedUserIds.includes(Number(userId))) {
                resolvedUserIds.push(Number(userId));
            }

            const newGroup = {
                groupName: newGroupName,
                groupDescription: newGroupDescription,
                userIds: resolvedUserIds,
            };

            await axios.post("http://localhost:8080/group/api/group/new", newGroup);

            setNewGroupName("");
            setNewGroupDescription("");
            setNewGroupMembers("");

            fetchGroups(); // Refresh the group list
        } catch (error) {
            console.error("Error creating group:", error);
        }
    };


    useEffect(() => {
        fetchGroups();
    }, [userId]);

    useEffect(() => {
        const allUserIds = groups.flatMap((group) => group.userIds);
        if (allUserIds.length > 0) {
            fetchUsernames(allUserIds);
        }
    }, [groups]);

    return (
        <div className="group-component">
            <TopBar />
            <div className="group-content">
                <div className="top-section">
                    <h1>Group Overview</h1>
                    <p>Here are all your groups. Click on a group below or create a new one.</p>
                </div>

                <div className="create-group-section">
                    <h2>Create a New Group</h2>
                    <input
                        className="group-input"
                        placeholder="Group Name"
                        value={newGroupName}
                        onChange={(e) => setNewGroupName(e.target.value)}
                    />
                    <textarea
                        className="group-input"
                        placeholder="Group Description"
                        value={newGroupDescription}
                        onChange={(e) => setNewGroupDescription(e.target.value)}
                    />
                    <input
                        className="group-input"
                        placeholder="Group Members (comma-separated usernames)"
                        value={newGroupMembers}
                        onChange={(e) => setNewGroupMembers(e.target.value)}
                    />
                    <button className="create-group-button" onClick={handleCreateGroup}>
                        Create Group
                    </button>
                </div>

                <div className="group-list">
                    {groups.map((group) => (
                        <div
                            key={group.groupID}
                            className="group-card"
                            onClick={() => groupNavigation(group.groupID)}
                        >
                            <h2>{group.groupName}</h2>
                            <p>{group.groupDescription}</p>
                            <p>Members:</p>
                            <ul>
                                {group.userIds.map((memberId) => (
                                    <li key={memberId}>{usernames[memberId] || "Loading..."}</li>
                                ))}
                            </ul>
                        </div>
                    ))}
                </div>
            </div>
            <BottomBar />
        </div>
    );
};

export default GroupComponent;
