import react from 'react';
import './GroupComponent.css';
import TopBar from "./TopBar";
import BottomBar from "./BottomBar";
import cookies from "js-cookie";
import axios from "axios";

const GroupComponent = () => {
    const [groups, setGroups] = react.useState([]);
    const [userId, setUserId] = react.useState(cookies.get("userid"));
    const [usernames, setUsernames] = react.useState({}); // To store usernames by user ID

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

    react.useEffect(() => {
        fetchGroups();
    }, [userId]);

    react.useEffect(() => {
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
                    <h1>Group overview</h1>
                    <p>Here are all your groups managed, click on a group below or create one</p>
                </div>
                <div className="group-list">
                    {groups.map((group) => (
                        <div
                            key={group.groupID}
                            className="group-card"
                            onClick={() => groupNavigation(group.groupID)} // Make the group card clickable
                            style={{ cursor: "pointer" }} // Add a pointer cursor for better UX
                        >
                            <h2>{group.groupName}</h2>
                            <p>{group.groupDescription}</p>
                            <p>Members:</p>
                            <ul>
                                {group.userIds.map((memberId) => (
                                    <li key={memberId}>
                                        {usernames[memberId] || "Loading..."}
                                    </li>
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
