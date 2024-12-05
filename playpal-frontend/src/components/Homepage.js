import React from "react";
import TopBar from "./TopBar";
import "./Homepage.css";
import BottomBar from "./BottomBar";

const Homepage = () => {
    const handleRunescapeClick = () => {
        // Navigate to Runescape or perform an action
        console.log("Runescape button clicked!");
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
