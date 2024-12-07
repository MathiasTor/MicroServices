import React from "react";
import TopBar from "./TopBar";
import "./Homepage.css";
import BottomBar from "./BottomBar";
import {useNavigate} from "react-router-dom";


const Homepage = () => {
    const navigate = useNavigate();
    const handleRunescapeClick = () => {
        navigate("/posts");
    };

    return (
        <div>
            <TopBar />
            <div className="homepage-container">
                <h1>Homepage</h1>
                <p>Browse available games:</p>
                <div className="games-list">
                    <button className="game-button" onClick={handleRunescapeClick}>
                        Runescape
                    </button>
                </div>
            </div>
            <BottomBar />
        </div>
    );
};

export default Homepage;
