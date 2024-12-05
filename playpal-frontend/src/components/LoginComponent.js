import React, {useEffect, useState} from "react";
import "./LoginComponent.css";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import cookies from "js-cookie";

const LoginComponent = ({  }) => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    const navigate = useNavigate();

    useEffect(() => {
        // Check if the user is already logged in
        const username = cookies.get("username");
        const userid = cookies.get("userid");

        if (username && userid) {
            navigate("/home");
        }
    }, []);

    const handleSubmit = (e) => {
        e.preventDefault();

        if (!username || !password) {
            setError("Both fields are required.");
            return;
        }

        onLogin(username, password);
        setError("");
    };

    const onLogin = (username, password) => {
        // Make the login request
        axios
            .post("http://localhost:8080/user/api/users/login", {
                username: username,
                password: password,
            })
            .then((response) => {
                console.log(response);
                console.log(response.status);

                // Check for a successful login
                if (response.status === 200) {
                    //Set cookies
                    cookies.set("username", username, { path: "/" });
                    cookies.set("userid", response.data.id, { path: "/" });

                    // Navigate to the homepage
                    navigate("/home");
                }
            })
            .catch((error) => {
                if (error.response && error.response.status === 401) {
                    console.log("Invalid credentials");
                    setError("Invalid credentials");
                } else {
                    console.log("An error occurred:", error.message);
                    setError("An error occurred. Please try again.");
                }
            });
    };

    return (
        <div className="login-container">
            <h2>Login</h2>
            <form onSubmit={handleSubmit} className="login-form">
                <div className="form-field">
                    <label htmlFor="username">Username:</label>
                    <input
                        type="text"
                        id="username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        placeholder="Enter your username"
                        className="input-field"
                    />
                </div>
                <div className="form-field">
                    <label htmlFor="password">Password:</label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Enter your password"
                        className="input-field"
                    />
                </div>
                {error && <p className="error-text">{error}</p>}
                <button type="submit" className="login-button">
                    Login
                </button>
            </form>
        </div>
    );
};

export default LoginComponent;
