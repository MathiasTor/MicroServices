import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import Cookies from "js-cookie";
import TopBar from "./TopBar";
import "./ProfileComponent.css";
import BottomBar from "./BottomBar";

const ProfileComponent = () => {
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [runescapeStats, setRunescapeStats] = useState(null);
    const [error, setError] = useState("");
    const [isOwnProfile, setIsOwnProfile] = useState(false);
    const [linked, setLinked] = useState(false);
    const [runescapeUsername, setRunescapeUsername] = useState("");
    const navigate = useNavigate();
    const { userId } = useParams();

    useEffect(() => {
        const loggedInUserId = Cookies.get("userid");

        if (!loggedInUserId) {
            setError("User is not logged in.");
            navigate("/login");
            return;
        }

        // Determine if viewing own profile
        const viewingOwnProfile = loggedInUserId === userId;
        setIsOwnProfile(viewingOwnProfile);

        // Fetch profile data (same endpoint)
        axios
            .get(`http://localhost:8080/user/api/users/specific/${userId}`)
            .then((response) => {
                setUsername(response.data.username);
                if (viewingOwnProfile) {
                    setEmail(response.data.email); // Only set email if viewing own profile
                }
            })
            .catch((error) => {
                console.error("Error fetching user details:", error);
                setError("Failed to fetch user details.");
            });

        // Fetch RuneScape account link status and stats
        axios
            .get(`http://localhost:8080/runescape/api/runescape/is-linked/${userId}`)
            .then((response) => {
                setLinked(response.data);
                if (response.data) {
                    axios
                        .get(`http://localhost:8080/runescape/api/runescape/get-stats/${userId}`)
                        .then((statsResponse) => {
                            setRunescapeStats(statsResponse.data);
                        })
                        .catch((statsError) => {
                            console.error("Error fetching RuneScape stats:", statsError);
                            setError("Failed to fetch RuneScape stats.");
                        });
                }
            })
            .catch((linkError) => {
                console.error("Error checking RuneScape link status:", linkError);
                setError("Failed to check RuneScape link status.");
            });
    }, [navigate, userId]);

    const handleLinkRunescape = () => {
        const loggedInUserId = Cookies.get("userid");
        axios
            .post(`http://localhost:8080/runescape/api/runescape/create/${loggedInUserId}/${runescapeUsername}`)
            .then(() => {
                setLinked(true);
                setError("");
            })
            .catch((error) => {
                console.error("Error linking RuneScape account:", error);
                setError("Failed to link RuneScape account.");
            });

        window.location.reload();
    };

    return (
        <div>
            <TopBar />
            <div className="profile-container">
                <h1>{isOwnProfile ? "Your profile" : `${username}'s profile`}</h1>
                {error ? (
                    <p className="error-text">{error}</p>
                ) : (
                    <>
                        <div className="profile-details">
                            <p>
                                <strong>Username:</strong> {username}
                            </p>
                            {isOwnProfile && (
                                <p>
                                    <strong>Email:</strong> {email}
                                </p>
                            )}
                        </div>
                        {isOwnProfile && !linked && (
                            <div className="link-runescape">
                                <h2>Link RuneScape Account</h2>
                                <input
                                    type="text"
                                    placeholder="RuneScape Username"
                                    value={runescapeUsername}
                                    onChange={(e) => setRunescapeUsername(e.target.value)}
                                />
                                <button onClick={handleLinkRunescape}>Link Account</button>
                            </div>
                        )}
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
            <BottomBar />
        </div>
    );
};

export default ProfileComponent;
