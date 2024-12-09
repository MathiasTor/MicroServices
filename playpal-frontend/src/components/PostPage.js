import React, { useState, useEffect } from "react";
import axios from "axios";
import Cookies from "js-cookie";
import "./PostPage.css";
import TopBar from "./TopBar";
import BottomBar from "./BottomBar";

const PostPage = () => {
    const [showForm, setShowForm] = useState(false);
    const [posts, setPosts] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [userId] = useState(Cookies.get("userid"));
    const [formData, setFormData] = useState({
        title: "",
        description: "",
        tags: "",
        videoGame: "RuneScape",
        live: true,
        userId: Cookies.get("userid"),
    });

    // Fetch posts
    useEffect(() => {
        const fetchPosts = async () => {
            try {
                const response = await axios.get(
                    `http://localhost:8080/search/api/search/posts/pageable?page=${page}&size=10`
                );
                const postsWithDetails = await Promise.all(
                    response.data.content.map(async (post) => {
                        const approvedUsers = await fetchApprovedUsers(post.id);
                        const applicants =
                            post.userId === parseInt(userId, 10)
                                ? await fetchApplicants(post.id)
                                : [];
                        return { ...post, applicants, approvedUsers };
                    })
                );
                setPosts(postsWithDetails);
                setTotalPages(response.data.totalPages);
            } catch (error) {
                console.error("Error fetching posts:", error);
            }
        };

        fetchPosts();
    }, [page, userId]);

    // Form submission
    const handleFormSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.post("http://localhost:8080/search/api/search/posts", formData);
            setShowForm(false);
            setPage(0);
            window.location.reload();
        } catch (error) {
            console.error("Error creating post:", error);
        }
    };

    // Fetch applicants
    const fetchApplicants = async (postId) => {
        try {
            const response = await axios.get(
                `http://localhost:8080/search/api/search/applicants/${postId}`
            );
            const applicantIds = response.data;

            const applicantsWithUsernames = await Promise.all(
                applicantIds.map(async (id) => {
                    const userResponse = await axios.get(
                        `http://localhost:8080/user/api/users/specific/${id}`
                    );
                    return { id, username: userResponse.data.username };
                })
            );

            return applicantsWithUsernames;
        } catch (error) {
            console.error("Error fetching applicants:", error);
            return [];
        }
    };

    // Stop search
    const stopSearch = async (postId) => {
        try {
            await axios.post(`http://localhost:8080/search/api/search/stop/${postId}`);
            setPosts((prevPosts) =>
                prevPosts.map((post) =>
                    post.id === postId ? { ...post, live: false } : post
                )
            );
        } catch (error) {
            console.error("Error stopping search:", error);
        }
    };

    // Apply to post
    const applyToPost = async (postId, userId) => {
        try {
            await axios.post(
                `http://localhost:8080/search/api/search/apply/${postId}/${userId}`
            );
            window.location.reload();
        } catch (error) {
            console.error("Error applying to post:", error);
        }
    };

    // Approve user
    const approveUser = async (postId, userId) => {
        try {
            await axios.post(
                `http://localhost:8080/search/api/search/approve/${postId}/${userId}`
            );

            const updatedApprovedUsers = await fetchApprovedUsers(postId);
            setPosts((prevPosts) =>
                prevPosts.map((post) =>
                    post.id === postId ? { ...post, approvedUsers: updatedApprovedUsers } : post
                )
            );
            window.location.reload();
        } catch (error) {
            console.error("Error approving user:", error);
        }
    };

    // Disapprove user
    const disapproveUser = async (postId, userId) => {
        try {
            await axios.post(
                `http://localhost:8080/search/api/search/disapprove/${postId}/${userId}`
            );
            window.location.reload();
        } catch (error) {
            console.error("Error disapproving user:", error);
        }
    };

    // Fetch approved users
    const fetchApprovedUsers = async (postId) => {
        try {
            const response = await axios.get(
                `http://localhost:8080/search/api/search/approved/${postId}`
            );
            const approvedUserIds = response.data;

            const approvedUsersWithUsernames = await Promise.all(
                approvedUserIds.map(async (id) => {
                    try {
                        const userResponse = await axios.get(
                            `http://localhost:8080/user/api/users/specific/${id}`
                        );
                        return { id, username: userResponse.data.username };
                    } catch (error) {
                        console.error(`Failed to fetch username for userId ${id}:`, error);
                        return { id, username: "Unknown User" };
                    }
                })
            );

            return approvedUsersWithUsernames;
        } catch (error) {
            console.error(`Error fetching approved users for postId ${postId}:`, error);
            return [];
        }
    };

    return (
        <div className="container">
            <div className="top-section">
                <TopBar />
                <button onClick={() => setShowForm(!showForm)}>
                    {showForm ? "Cancel" : "Create Post"}
                </button>
            </div>
            <div className="main-content">
                {showForm && (
                    <form onSubmit={handleFormSubmit} className="form-container">
                        <label>Title:</label>
                        <input
                            type="text"
                            value={formData.title}
                            onChange={(e) =>
                                setFormData({ ...formData, title: e.target.value })
                            }
                            required
                        />
                        <label>Description:</label>
                        <textarea
                            value={formData.description}
                            onChange={(e) =>
                                setFormData({ ...formData, description: e.target.value })
                            }
                            required
                        />
                        <label>Tags (comma-separated):</label>
                        <input
                            type="text"
                            value={formData.tags}
                            onChange={(e) =>
                                setFormData({ ...formData, tags: e.target.value })
                            }
                        />
                        <button type="submit">Submit</button>
                    </form>
                )}

                <h3>Posts</h3>
                {posts.slice().reverse().map((post) => (
                    <div key={post.id} className="post">
                        {post.live && <span className="live-indicator">Active</span>}
                        <h4>{post.title}</h4>
                        <p>{post.description}</p>
                        <p>
                            Created by: <UsernameLink userId={post.userId} />
                        </p>
                        <small>Tags: {post.tags}</small>

                        <div className="approved-users-section">
                            <h5>Approved Users:</h5>
                            {post.approvedUsers && post.approvedUsers.length > 0 ? (
                                post.approvedUsers.map((user, index) => (
                                    <div key={`${user.id}-${index}`} className="approved-user">
                                        <UsernameLink userId={user.id} />
                                    </div>
                                ))
                            ) : (
                                <p>No approved users yet.</p>
                            )}
                        </div>

                        {parseInt(userId, 10) === post.userId && post.live && (
                            <button
                                className="stop-search-button"
                                onClick={() => stopSearch(post.id)}
                            >
                                Stop Search
                            </button>
                        )}

                        {post.live && parseInt(userId, 10) !== post.userId && (
                            <button
                                className="apply-button"
                                onClick={() => applyToPost(post.id, parseInt(userId, 10))}
                            >
                                Apply
                            </button>
                        )}

                        {parseInt(userId, 10) === post.userId && (
                            <div className="applicants-section">
                                <h5>Applicants:</h5>
                                {post.applicants && post.applicants.length > 0 ? (
                                    post.applicants.map((applicant) => (
                                        <div key={applicant.id} className="applicant">
                                            <UsernameLink userId={applicant.id} />
                                            <button
                                                className="approve-button"
                                                onClick={() =>
                                                    approveUser(post.id, applicant.id)
                                                }
                                            >
                                                Approve
                                            </button>
                                            <button
                                                className="disapprove-button"
                                                onClick={() =>
                                                    disapproveUser(post.id, applicant.id)
                                                }
                                            >
                                                Disapprove
                                            </button>
                                        </div>
                                    ))
                                ) : (
                                    <p>No applicants yet.</p>
                                )}
                            </div>
                        )}
                    </div>
                ))}
                <div className="pagination">
                    <button
                        onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
                        disabled={page === 0}
                    >
                        Previous
                    </button>
                    <span>
                        Page {page + 1} of {totalPages}
                    </span>
                    <button
                        onClick={() =>
                            setPage((prev) =>
                                prev + 1 < totalPages ? prev + 1 : prev
                            )
                        }
                        disabled={page + 1 >= totalPages}
                    >
                        Next
                    </button>
                </div>
            </div>
            <BottomBar />
        </div>
    );
};

const UsernameLink = ({ userId }) => {
    const [username, setUsername] = useState("Loading...");

    useEffect(() => {
        const fetchUsername = async () => {
            try {
                const response = await axios.get(
                    `http://localhost:8080/user/api/users/specific/${userId}`
                );
                setUsername(response.data.username);
            } catch (error) {
                console.error(`Failed to fetch username for userId ${userId}:`, error);
                setUsername("Unknown User");
            }
        };

        fetchUsername();
    }, [userId]);

    return (
        <a href={`/profile/${userId}`} target="_blank" rel="noopener noreferrer">
            {username}
        </a>
    );
};

export default PostPage;
