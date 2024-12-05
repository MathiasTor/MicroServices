import React, { useEffect, useState } from "react";
import "./LoginComponent.css";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import cookies from "js-cookie";

const LoginComponent = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [email, setEmail] = useState(""); // New state for email
    const [isRegistering, setIsRegistering] = useState(false); // Toggles between login and register
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const navigate = useNavigate();

    useEffect(() => {
        // Check if the user is already logged in
        const username = cookies.get("username");
        const userid = cookies.get("userid");

        if (username && userid) {
            navigate("/home");
        }
    }, []);

    const handleLoginSubmit = (e) => {
        e.preventDefault();

        if (!username || !password) {
            setError("Both fields are required.");
            return;
        }

        login(username, password);
        setError("");
    };

    const handleRegisterSubmit = (e) => {
        e.preventDefault();

        if (!username || !email || !password) {
            setError("All fields are required.");
            return;
        }

        register(username, email, password);
        setError("");
    };

    const login = (username, password) => {
        axios
            .post("http://localhost:8080/user/api/users/login", {
                username: username,
                password: password,
            })
            .then((response) => {
                console.log(response);
                console.log(response.status);

                if (response.status === 200) {
                    cookies.set("username", username, { path: "/" });
                    cookies.set("userid", response.data.id, { path: "/" });
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

    const register = (username, email, password) => {
        axios
            .post("http://localhost:8080/user/api/users", {
                username: username,
                email: email,
                password: password,
            })
            .then((response) => {
                console.log(response);
                setSuccess("Registration successful! You can now log in.");
                setIsRegistering(false);
            })
            .catch((error) => {
                console.log("An error occurred:", error.message);
                setError("Registration failed. Please try again.");
            });
    };

    return (
        <div className="login-container">
            <h2>{isRegistering ? "Register" : "Login"}</h2>
            {success && <p className="success-text">{success}</p>}
            {error && <p className="error-text">{error}</p>}

            {!isRegistering ? (
                <form onSubmit={handleLoginSubmit} className="login-form">
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
                    <button type="submit" className="login-button">Login</button>
                    <p className="toggle-text">
                        Don't have an account?{" "}
                        <span onClick={() => setIsRegistering(true)} className="toggle-link">
                            Register
                        </span>
                    </p>
                </form>
            ) : (
                <form onSubmit={handleRegisterSubmit} className="register-form">
                    <div className="form-field">
                        <label htmlFor="username">Username:</label>
                        <input
                            type="text"
                            id="username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            placeholder="Choose a username"
                            className="input-field"
                        />
                    </div>
                    <div className="form-field">
                        <label htmlFor="email">Email:</label>
                        <input
                            type="email"
                            id="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="Enter your email"
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
                            placeholder="Choose a password"
                            className="input-field"
                        />
                    </div>
                    <button type="submit" className="register-button">Register</button>
                    <p className="toggle-text">
                        Already have an account?{" "}
                        <span onClick={() => setIsRegistering(false)} className="toggle-link">
                            Login
                        </span>
                    </p>
                </form>
            )}
        </div>
    );
};

export default LoginComponent;
