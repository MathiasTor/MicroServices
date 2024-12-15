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
    const [aiPictureInput, setAIPictureInput] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();
    const { userId } = useParams();
    const [profilePicture, setProfilePicture] = useState(null);
    const [previewImage, setPreviewImage] = useState(null);

    useEffect(() => {
        const loggedInUserId = Cookies.get("userid");

        if (!loggedInUserId) {
            setError("User is not logged in.");
            navigate("/");
            return;
        }

        const viewingOwnProfile = loggedInUserId === userId;
        setIsOwnProfile(viewingOwnProfile);

        axios
            .get(`http://localhost:8080/profile/api/profiles/${userId}`)
            .then((response) => {
                setProfileData(response.data);
                setNewBio(response.data.bio || "");
            })
            .catch((error) => {
                console.error("Error fetching profile data:", error);
                setError("Failed to fetch profile data.");
            });

        if (viewingOwnProfile) {
            axios
                .get(`http://localhost:8080/user/api/users/specific/${userId}`)
                .then((response) => {
                    setUserProfileData(response.data);
                    setEmail(response.data.email);
                })
                .catch((error) => {
                    console.error("Error fetching user details:", error);
                    setError("Failed to fetch user details.");
                });

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

    const handleGenerateAIPicture = async () => {
        setIsLoading(true);
        try {
            const response = await axios.post(
                `http://localhost:8080/profile/api/profiles/generate-ai-picture/${userId}`,
                { prompt: aiPictureInput }
            );
            setProfileData((prevData) => ({
                ...prevData,
                profilePictureUrl: response.data.image_urls[0],
            }));
            setIsLoading(false);
            window.location.reload();
        } catch (error) {
            console.error("Failed to generate AI picture:", error);
            setError("Failed to generate AI picture.");
            setIsLoading(false);
        }
    };

    const handleVote = async (voteType) => {
        const loggedInUserId = Cookies.get("userid");

        if (!loggedInUserId) {
            setError("You need to be logged in to vote.");
            return;
        }

        try {
            const response = await axios.post(
                `http://localhost:8080/profile/api/profiles/${userId}/vote`,
                null,
                {
                    params: {
                        voterId: loggedInUserId,
                        voteType: voteType,
                    },
                }
            );
            setProfileData(response.data);
        } catch (error) {
            console.error("Failed to cast vote:", error);
            setError("Failed to cast vote.");
        }
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
                            <div className="profile-details">
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
                                        <div className="bio-edit-container">
                                            <textarea
                                                rows="6"
                                                value={newBio}
                                                onChange={(e) => setNewBio(e.target.value)}
                                                className="bio-textarea"
                                            />
                                            <button onClick={handleBioUpdate} className="save-button">
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
                        {isLoading ? (
                            <div className="loading-indicator">Loading...</div>
                        ) : (
                            <img src={profileData?.profilePictureUrl || "/default-profile.png"} alt="Profile" />
                        )}
                        {isOwnProfile && previewImage && (
                            <img src={previewImage} alt="Preview" className="preview-image" />
                        )}
                    </div>
                </div>
                {isOwnProfile ? (
                    <>
                        <button onClick={handleEditToggle} className="edit-button">
                            {isEditing ? "Cancel" : "Edit Profile"}
                        </button>
                        {isEditing && (
                            <div className="ai-picture-generator">
                                <input
                                    type="text"
                                    placeholder="Prompt"
                                    value={aiPictureInput}
                                    onChange={(e) => setAIPictureInput(e.target.value)}
                                    className="ai-input"
                                />
                                <button onClick={handleGenerateAIPicture} className="generate-button">
                                    Generate AI Picture
                                </button>
                            </div>
                        )}
                    </>
                ) : (
                    <div className="vote-buttons">
                        <button
                            onClick={() => handleVote("UPVOTE")}
                            className="upvote-button"
                        >
                            👍
                        </button>
                        <button
                            onClick={() => handleVote("DOWNVOTE")}
                            className="downvote-button"
                        >
                            👎
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
            <BottomBar />
        </div>
    );
};

export default ProfileComponent;
