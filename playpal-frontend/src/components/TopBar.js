import React from "react";
import "./TopBar.css";
import cookies from "js-cookie";
import {navigate, useNavigate} from "react-router-dom";

const TopBar = () => {

    const navigate = useNavigate();
    function HandleLogout() {
        // Remove cookies
        cookies.remove("username");
        cookies.remove("userid");

        // Redirect to the login page
        navigate("/");
    }

    function HandleProfile() {
        const id = cookies.get("userid");
        navigate(`/profile/${id}`);
    }

    function HandleHome() {
        navigate("/home");
    }

    function HandleGroup() {
        navigate("/groups");
    }

    function HandleLeaderboard() {
        navigate("/leaderboard");
    }

    return (
        <div className="top-bar">
            <h1 className="logo">PlayPal</h1>
            <div className="nav-buttons">
                <button className="nav-button" onClick={HandleHome}>Home</button>
                <button className="nav-button" onClick={HandleGroup}> Groups</button>
                <button className="nav-button" onClick={HandleLeaderboard}> Leaderboard</button>
                <button className="nav-button" onClick={HandleProfile}> Profile</button>
                <button className="nav-button" onClick={HandleLogout}> Logout</button>
            </div>
        </div>
    );
};

export default TopBar;
