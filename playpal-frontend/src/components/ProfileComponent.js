import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import Cookies from "js-cookie";
import TopBar from "./TopBar";
import "./ProfileComponent.css";

const ProfileComponent = () => {
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [runescapeStats, setRunescapeStats] = useState(null);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        const userId = Cookies.get("userid");

        if (!userId) {
            setError("User is not logged in.");
            navigate("/login"); // Redirect to login if not logged in
            return;
        }

        // Fetch user details
        axios
            .get(`http://localhost:8080/user/api/users/specific/${userId}`)
            .then((response) => {
                setUsername(response.data.username);
                setEmail(response.data.email);
            })
            .catch((error) => {
                console.error("Error fetching user details:", error);
                setError("Failed to fetch user details.");
            });

        // Fetch RuneScape stats
        axios
            .get(`http://localhost:8080/runescape/api/runescape/get-stats/${userId}`)
            .then((response) => {
                setRunescapeStats(response.data);
            })
            .catch((error) => {
                console.error("Error fetching RuneScape stats:", error);
                setError("Failed to fetch RuneScape stats.");
            });
    }, [navigate]);

    return (
        <div>
            <TopBar />
            <div className="profile-container">
                <h1>Profile</h1>
                {error ? (
                    <p className="error-text">{error}</p>
                ) : (
                    <>
                        <div className="profile-details">
                            <p>
                                <strong>Username:</strong> {username}
                            </p>
                            <p>
                                <strong>Email:</strong> {email}
                            </p>
                        </div>
                        {runescapeStats && (
                            <div className="stats-container">
                                <div className="runescape-stats">
                                    <h2>RuneScape Stats</h2>
                                    <p>
                                        <strong>RuneScape Name:</strong> {runescapeStats.runescapeName}
                                    </p>
                                    <p>
                                        <strong>Total Level:</strong> {runescapeStats.total}
                                    </p>
                                    <ul>
                                        <li><strong>Attack:</strong> {runescapeStats.attack}</li>
                                        <li><strong>Defence:</strong> {runescapeStats.defence}</li>
                                        <li><strong>Strength:</strong> {runescapeStats.strength}</li>
                                        <li><strong>Hitpoints:</strong> {runescapeStats.hitpoints}</li>
                                        <li><strong>Ranged:</strong> {runescapeStats.ranged}</li>
                                        <li><strong>Prayer:</strong> {runescapeStats.prayer}</li>
                                        <li><strong>Magic:</strong> {runescapeStats.magic}</li>
                                        <li><strong>Cooking:</strong> {runescapeStats.cooking}</li>
                                        <li><strong>Woodcutting:</strong> {runescapeStats.woodcutting}</li>
                                        <li><strong>Fletching:</strong> {runescapeStats.fletching}</li>
                                        <li><strong>Fishing:</strong> {runescapeStats.fishing}</li>
                                        <li><strong>Firemaking:</strong> {runescapeStats.firemaking}</li>
                                        <li><strong>Crafting:</strong> {runescapeStats.crafting}</li>
                                        <li><strong>Smithing:</strong> {runescapeStats.smithing}</li>
                                        <li><strong>Mining:</strong> {runescapeStats.mining}</li>
                                        <li><strong>Herblore:</strong> {runescapeStats.herblore}</li>
                                        <li><strong>Agility:</strong> {runescapeStats.agility}</li>
                                        <li><strong>Thieving:</strong> {runescapeStats.thieving}</li>
                                        <li><strong>Slayer:</strong> {runescapeStats.slayer}</li>
                                        <li><strong>Farming:</strong> {runescapeStats.farming}</li>
                                        <li><strong>Runecrafting:</strong> {runescapeStats.runecrafting}</li>
                                        <li><strong>Hunter:</strong> {runescapeStats.hunter}</li>
                                        <li><strong>Construction:</strong> {runescapeStats.construction}</li>
                                    </ul>
                                </div>
                                <div className="raids-kc">
                                    <h2>Raids KC</h2>
                                    <p><strong>Tombs of Amascut:</strong> {runescapeStats.toaKC}</p>
                                    <p><strong>Chambers of Xeric:</strong> {runescapeStats.coxKC}</p>
                                    <p><strong>Theatre of Blood:</strong> {runescapeStats.tobKC}</p>
                                </div>
                            </div>
                        )}
                    </>
                )}
            </div>
        </div>
    );
};

export default ProfileComponent;
