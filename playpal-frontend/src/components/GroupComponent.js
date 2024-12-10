import react from 'react';
import './GroupComponent.css';
import TopBar from "./TopBar";
import BottomBar from "./BottomBar";
import cookies from "js-cookie";
import axios from "axios";

const GroupComponent = () => {
    const [groups, setGroups] = react.useState([]);
    const [userId, setUserId] = react.useState(cookies.get("userid"));
    const fetchGroups = async () => {
        const response = await axios.get(`http://localhost:8080/group/api/user/${userId}`);
        console.log(response);
    }

    return (
        <div className="group-component">
            <TopBar />
            <div className="group-content">
                <div className="top-section">
                    <h1>Group overview</h1>
                    <p>Here are all your groups managed, click on a group below or create one</p>
                </div>
            </div>
            <BottomBar />
        </div>
    );
}

export default GroupComponent;