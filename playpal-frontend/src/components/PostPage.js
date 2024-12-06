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
    const [usernames, setUsernames] = useState({});
    const [formData, setFormData] = useState({
        title: "",
        description: "",
        tags: "",
        videoGame: "RuneScape",
        live: false,
        userId: Cookies.get("userid"),
    });

    // Fetch posts and usernames
    useEffect(() => {
        const fetchPostsAndUsernames = async () => {
            try {
                const response = await axios.get(
                    `http://localhost:8080/search/api/search/posts/pageable?page=${page}&size=10`
                );
                setPosts(response.data.content);
                setTotalPages(response.data.totalPages);

                // Fetch usernames for posts
                const uniqueUserIds = [
                    ...new Set(response.data.content.map((post) => post.userId)),
                ];

                // Fetch usernames for unique user IDs
                const usernamesMap = { ...usernames }; // Copy existing usernames to avoid overwriting
                await Promise.all(
                    uniqueUserIds.map(async (userId) => {
                        if (!usernames[userId]) {
                            const usernameResponse = await axios.get(
                                `http://localhost:8080/user/api/users/${userId}`
                            );
                            usernamesMap[userId] = usernameResponse.data.username;
                        }
                    })
                );

                setUsernames(usernamesMap); // Update state with new usernames
            } catch (error) {
                console.error("Error fetching posts or usernames:", error);
            }
        };

        fetchPostsAndUsernames();
    }, [page]);

    const handleFormSubmit = async (e) => {
        e.preventDefault();
        try {
            console.log("Creating post:", formData);
            await axios.post("http://localhost:8080/search/api/search/posts", formData);
            alert("Post created successfully!");
            setShowForm(false);
            setPage(0);
        } catch (error) {
            console.error("Error creating post:", error);
            alert("Failed to create post");
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
                            onChange={(e) => setFormData({ ...formData, title: e.target.value })}
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
                            onChange={(e) => setFormData({ ...formData, tags: e.target.value })}
                        />
                        <button type="submit">Submit</button>
                    </form>
                )}

                <h3>Posts</h3>
                {posts.length > 0 ? (
                    posts.map((post) => (
                        <div key={post.id} className="post">
                            <h4>{post.title}</h4>
                            <p>{post.description}</p>
                            <p>
                                Created by:{" "}
                                {usernames[post.userId] ? usernames[post.userId] : "Loading..."}
                            </p>
                            <small>Tags: {post.tags}</small>
                        </div>
                    ))
                ) : (
                    <p>No posts available</p>
                )}
                <div className="pagination">
                    <button onClick={() => setPage((prev) => Math.max(prev - 1, 0))} disabled={page === 0}>
                        Previous
                    </button>
                    <span>
            Page {page + 1} of {totalPages}
          </span>
                    <button
                        onClick={() => setPage((prev) => (prev + 1 < totalPages ? prev + 1 : prev))}
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

export default PostPage;
