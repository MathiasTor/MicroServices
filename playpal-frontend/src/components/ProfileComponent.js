import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import Cookies from "js-cookie";
import TopBar from "./TopBar";
import "./ProfileComponent.css";
import BottomBar from "./BottomBar";

const ProfileComponent = () => {
    const [userProfileData, setUserProfileData] = useState(null);
    const [profileData, setProfileData] = useState(null);
    const [email, setEmail] = useState("");
    const [runescapeStats, setRunescapeStats] = useState(null);
    const [error, setError] = useState("");
    const [isOwnProfile, setIsOwnProfile] = useState(false);
    const [linked, setLinked] = useState(false);
    const [runescapeUsername, setRunescapeUsername] = useState("");
    const [isEditing, setIsEditing] = useState(false);
    const [newBio, setNewBio] = useState("");
    const navigate = useNavigate();
    const { userId } = useParams();

    useEffect(() => {
        const loggedInUserId = Cookies.get("userid");

        if (!loggedInUserId) {
            setError("User is not logged in.");
            navigate("/");
            return;
        }

        // Determine if viewing own profile
        const viewingOwnProfile = loggedInUserId === userId;
        setIsOwnProfile(viewingOwnProfile);

        // Fetch profile data for all profiles
        axios
            .get(`http://localhost:8080/profile/api/profiles/${userId}`)
            .then((response) => {
                setProfileData(response.data);
                setNewBio(response.data.bio || ""); // Set initial bio value
            })
            .catch((error) => {
                console.error("Error fetching profile data:", error);
                setError("Failed to fetch profile data.");
            });

        // Fetch user-specific data only for own profile
        if (viewingOwnProfile) {
            axios
                .get(`http://localhost:8080/user/api/users/specific/${userId}`)
                .then((response) => {
                    setUserProfileData(response.data);
                    setEmail(response.data.email); // Only set email if viewing own profile
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
        } else {
            // Fetch RuneScape stats for other profiles if linked
            axios
                .get(`http://localhost:8080/runescape/api/runescape/is-linked/${userId}`)
                .then((response) => {
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
        }
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

        if (navigator.userAgent.toLowerCase().indexOf("firefox") > -1) {
        } else {
            window.location.reload();
        }
    };


    const handleBioUpdate = () => {
        axios
            .put(`http://localhost:8080/profile/api/profiles/${userId}`, { bio: newBio })
            .then((response) => {
                setProfileData((prevData) => ({
                    ...prevData,
                    bio: response.data.bio,
                }));
                setIsEditing(false);
                setError("");
            })
            .catch((error) => {
                console.error("Error updating bio:", error);
                setError("Failed to update bio.");
            });
    };

    const handleEditToggle = () => {
        setIsEditing((prev) => !prev);
    };

    return (
        <div>
            <TopBar />
            <div className="profile-container">
                <div className="profile-header">
                    <div className="profile-info">
                        <h1>{isOwnProfile ? "Your Profile" : `${profileData?.name || "User"}'s Profile`}</h1>
                        {profileData && (
                            <div className="profile-details"
                                 style={{wordWrap: "break-word", overflowWrap: "break-word", maxWidth: "475px"}}>
                                <p>
                                    <strong>Username:</strong> {profileData.name}
                                </p>
                                <p>
                                    {isOwnProfile && (
                                        <p>
                                            <strong>Email:</strong> {email}
                                        </p>
                                    )}
                                    <strong>Bio:</strong>{" "}
                                    {isEditing ? (
                                        <div style={{position: "relative", width: "100%", marginTop: "10px"}}>
        <textarea
            rows="6"
            value={newBio}
            onChange={(e) => setNewBio(e.target.value)}
            style={{
                width: "100%",
                resize: "none",
                padding: "10px",
                borderRadius: "5px",
                border: "1px solid #ccc",
                boxSizing: "border-box",
            }}
        />
                                            <button
                                                onClick={handleBioUpdate}
                                                style={{
                                                    position: "absolute",
                                                    bottom: "10px",
                                                    right: "10px",
                                                    padding: "8px 16px",
                                                    backgroundColor: "#007bff",
                                                    color: "white",
                                                    border: "none",
                                                    borderRadius: "5px",
                                                    cursor: "pointer",
                                                }}
                                            >
                                                Save
                                            </button>
                                        </div>
                                    ) : (
                                        <>{profileData.bio || "No bio available."}</>
                                    )}
                                </p>
                                <p>
                                    <strong>Upvotes:</strong> {profileData.upvotes}
                                </p>
                                <p>
                                    <strong>Downvotes:</strong> {profileData.downvotes}
                                </p>
                            </div>
                        )}
                    </div>
                    <div className="profile-picture">
                    <img
                            src={profileData?.profilePictureUrl || "/default-profile.png"}
                            alt="Profile"
                            style={{
                                position: "absolute",
                                marginTop: "-14%",
                                marginLeft: "30%",
                                width: "250px",
                                height: isOwnProfile ? "250px" : "200px",
                                backgroundColor: "#f0f0f0",
                                border: "2px solid #ddd",
                                borderRadius: "10px",
                                textAlign: "center",
                                objectFit: "cover",
                                boxShadow: "0 0 10px rgba(0, 0, 0, 0.1)",
                            }}
                        />
                        {isOwnProfile && (
                            <button
                                style={{
                                    position: "relative",
                                    bottom: "10px",
                                    padding: "5px 10px",
                                    backgroundColor: "#007bff",
                                    color: "white",
                                    border: "none",
                                    borderRadius: "5px",
                                    cursor: "pointer",
                                    fontSize: "14px"
                                }}
                            >
                                Change Picture
                            </button>
                        )}
                    </div>
                </div>
                {isOwnProfile && (
                    <button
                        onClick={handleEditToggle}
                        style={{
                            position: "relative",
                            marginTop: "20px",
                            padding: "10px 20px",
                            backgroundColor: "#007bff",
                            color: "#fff",
                            border: "none",
                            borderRadius: "5px",
                            cursor: "pointer",
                            alignSelf: "end",
                        }}
                    >
                        {isEditing ? "Cancel" : "Edit Profile"}
                    </button>
                )}
                {isOwnProfile && !linked && (
                    <div className="link-runescape" style={{ marginTop: "20px" }}>
                        <h2>Link RuneScape Account</h2>
                        <input
                            type="text"
                            placeholder="RuneScape Username"
                            value={runescapeUsername}
                            onChange={(e) => setRunescapeUsername(e.target.value)}
                            style={{ padding: "5px", width: "300px" }}
                        />
                        <button onClick={handleLinkRunescape} style={{ marginLeft: "10px" }}>
                            Link Account
                        </button>
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
            </div>
            <BottomBar/>
        </div>
    );
};

export default ProfileComponent;
