import React, { useEffect, useState } from "react";
import axios from "axios";
import "./Leaderboard.css";
import TopBar from "./TopBar";
import BottomBar from "./BottomBar";

const Leaderboard = () => {
    const [totalLeaderboard, setTotalLeaderboard] = useState([]);
    const [weeklyLeaderboard, setWeeklyLeaderboard] = useState([]);

    // Base URL for the backend API
    const baseURL = "http://localhost:8080/leaderboard/api/leaderboard";

    // Fetch Total Leaderboard
    const fetchTotalLeaderboard = async () => {
        try {
            const response = await axios.get(`${baseURL}/total`);
            setTotalLeaderboard(response.data);
        } catch (error) {
            console.error("Error fetching total leaderboard:", error);
        }
    };

    // Fetch Weekly Leaderboard
    const fetchWeeklyLeaderboard = async () => {
        try {
            const response = await axios.get(`${baseURL}/weekly`);
            setWeeklyLeaderboard(response.data);
        } catch (error) {
            console.error("Error fetching weekly leaderboard:", error);
        }
    };


    const calculatePercentage = (value, maxValue) => {
        return maxValue > 0 ? (value / maxValue) * 100 : 0;
    };


    useEffect(() => {
        fetchTotalLeaderboard();
        fetchWeeklyLeaderboard();
    }, []);

    return (
        <div className="leaderboard-content">
            <TopBar />
            <div className="leaderboard-container">
                <h1>Leaderboard</h1>

                {/* Total Leaderboard */}
                <div className="leaderboard-section">
                    <h2>Total Leaderboard</h2>
                    <ul className="leaderboard-list">
                        {totalLeaderboard.map((entry, index) => {
                            const maxRaids =
                                totalLeaderboard.length > 0
                                    ? totalLeaderboard[0].totalRaids
                                    : 0;
                            const percentage = calculatePercentage(
                                entry.totalRaids,
                                maxRaids
                            );
                            return (
                                <li
                                    key={entry.userId}
                                    className="leaderboard-item"
                                >
                                    <span className="leaderboard-name">
                                        {entry.runescapeName}
                                    </span>
                                    <div className="progress-bar-container">
                                        <div
                                            className="progress-bar"
                                            style={{
                                                width: `${percentage}%`,
                                            }}
                                        ></div>
                                    </div>
                                    <span className="leaderboard-stats">
                                        {entry.totalRaids} raids
                                    </span>
                                </li>
                            );
                        })}
                    </ul>
                </div>

                {/* Weekly Leaderboard */}
                <div className="leaderboard-section">
                    <h2>Weekly Leaderboard</h2>
                    <ul className="leaderboard-list">
                        {weeklyLeaderboard.map((entry, index) => {
                            const totalWeeklyRaids =
                                entry.weeklyToaKC +
                                entry.weeklyCoxKC +
                                entry.weeklyTobKC;
                            const maxWeeklyRaids =
                                weeklyLeaderboard.length > 0
                                    ? Math.max(
                                        ...weeklyLeaderboard.map(
                                            (user) =>
                                                user.weeklyToaKC +
                                                user.weeklyCoxKC +
                                                user.weeklyTobKC
                                        )
                                    )
                                    : 0;
                            const percentage = calculatePercentage(
                                totalWeeklyRaids,
                                maxWeeklyRaids
                            );
                            return (
                                <li
                                    key={entry.userId}
                                    className="leaderboard-item"
                                >
                                    <span className="leaderboard-name">
                                        {entry.runescapeName}
                                    </span>
                                    <div className="progress-bar-container">
                                        <div
                                            className="progress-bar"
                                            style={{
                                                width: `${percentage}%`,
                                            }}
                                        ></div>
                                    </div>
                                    <span className="leaderboard-stats">
                                        TOA: {entry.weeklyToaKC}, COX:{" "}
                                        {entry.weeklyCoxKC}, TOB:{" "}
                                        {entry.weeklyTobKC}
                                    </span>
                                </li>
                            );
                        })}
                    </ul>
                </div>
            </div>
            <BottomBar />
        </div>
    );
};

export default Leaderboard;
