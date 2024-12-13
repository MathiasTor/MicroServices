import React, { useState, useEffect, useRef } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import Cookies from "js-cookie";
import TopBar from "./TopBar";
import BottomBar from "./BottomBar";
import "./GroupOverviewComponent.css";

const GroupOverviewComponent = () => {
    const { groupId } = useParams();
    const [group, setGroup] = useState(null);
    const [usernames, setUsernames] = useState({}); // Map of userId to username
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState("");
    const messagesEndRef = useRef(null);
    const stompClientRef = useRef(null);
    const userId = Cookies.get("userid");

    // Fetch usernames for a list of userIds once
    const fetchUsernamesOnce = async (userIds) => {
        try {
            if (!userIds || userIds.length === 0) return;

            const response = await axios.post("http://localhost:8080/user/api/users/bulk", userIds, {
                headers: { "Content-Type": "application/json" },
            });

            const usernameMap = response.data.reduce((map, user) => {
                map[user.id] = user.username;
                return map;
            }, {});

            setUsernames((prevUsernames) => ({ ...prevUsernames, ...usernameMap }));
        } catch (error) {
            console.error("Error fetching usernames:", error);
        }
    };

    // Fetch group details based on the groupId
    const fetchGroupDetails = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/group/api/group/id/${groupId}`);
            setGroup(response.data);

            // Fetch usernames for group members once
            const userIds = response.data.userIds;
            fetchUsernamesOnce(userIds);
        } catch (error) {
            console.error("Error fetching group details:", error);
        }
    };

    // Fetch chat messages for the group
    const fetchMessages = async () => {
        try {
            const response = await axios.get(
                `http://localhost:8080/communication/api/conversations/${groupId}/messages`
            );
            setMessages(response.data);

            // Extract unique senderIds and fetch their usernames once
            const senderIds = [...new Set(response.data.map((msg) => msg.senderId))];
            fetchUsernamesOnce(senderIds);
        } catch (error) {
            console.error("Error fetching messages:", error);
        }
    };

    // Connect to WebSocket for real-time chat
    const connectWebSocket = () => {
        const socket = new SockJS("http://localhost:8080/communication/ws/");
        const stompClient = new Client({
            webSocketFactory: () => socket,
            debug: (str) => console.log(str),
            onConnect: () => {
                console.log("Connected to WebSocket");

                stompClient.subscribe(`/topic/conversations/${groupId}`, (message) => {
                    const receivedMessage = JSON.parse(message.body);
                    console.log("Received message:", receivedMessage);

                    // Add the message to the state
                    setMessages((prevMessages) => {
                        const exists = prevMessages.some((msg) => msg.id === receivedMessage.id);
                        return exists ? prevMessages : [...prevMessages, receivedMessage];
                    });
                });
            },
        });

        stompClient.activate();
        stompClientRef.current = stompClient;

        return () => {
            if (stompClientRef.current) {
                stompClientRef.current.deactivate();
            }
        };
    };

    // Send a new message
    const sendMessage = () => {
        const stompClient = stompClientRef.current;
        if (stompClient && stompClient.connected) {
            const messageDTO = {
                conversationId: groupId,
                senderId: userId,
                content: newMessage,
            };

            stompClient.publish({
                destination: `/api/sendMessage/${groupId}`,
                body: JSON.stringify(messageDTO),
            });

            console.log("Message sent:", messageDTO);

            setNewMessage("");
        } else {
            console.error("Cannot send message: WebSocket is not connected.");
        }
    };

    // Scroll to the bottom of the chat
    const scrollToBottom = () => {
        if (messagesEndRef.current) {
            messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
        }
    };

    useEffect(() => {
        fetchGroupDetails();
        fetchMessages();
    }, [groupId]);

    useEffect(() => {
        connectWebSocket();
        return () => {
            if (stompClientRef.current) {
                stompClientRef.current.deactivate();
            }
        };
    }, [groupId]);

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    return (
        <div className="group-overview-component">
            <TopBar />
            <div className="group-overview-content">
                {group ? (
                    <>
                        <h1>{group.groupName}</h1>
                        <p>{group.groupDescription}</p>
                        <h3>Members:</h3>
                        <ul>
                            {group.userIds.map((id) => (
                                <li key={id}>{usernames[id] || `User ID: ${id}`}</li>
                            ))}
                        </ul>
                        <div className="chat-section">
                            <h2>Group Chat</h2>
                            <div className="chat-messages">
                                {messages.length === 0 ? (
                                    <p>No messages yet.</p>
                                ) : (
                                    messages.map((msg) => (
                                        <div
                                            key={msg.id}
                                            className={`chat-message ${
                                                String(msg.senderId) === String(userId) ? "chat-message-right" : "chat-message-left"
                                            }`}
                                        >
                                            <strong>
                                                {usernames[msg.senderId] || `User ${msg.senderId}`}:
                                            </strong>{" "}
                                            {msg.content}
                                        </div>
                                    ))
                                )}
                                <div ref={messagesEndRef} />
                            </div>
                            <div className="chat-input">
                                <input
                                    type="text"
                                    value={newMessage}
                                    onChange={(e) => setNewMessage(e.target.value)}
                                    placeholder="Type a message..."
                                    onKeyDown={(e) => {
                                        if (e.key === "Enter") {
                                            sendMessage();
                                        }
                                    }}
                                />
                                <button onClick={sendMessage}>Send</button>
                            </div>
                        </div>
                    </>
                ) : (
                    <p>Loading group details...</p>
                )}
            </div>
            <BottomBar />
        </div>
    );
};

export default GroupOverviewComponent;
