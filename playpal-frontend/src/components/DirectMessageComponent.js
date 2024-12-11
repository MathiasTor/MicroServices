import React, { useState, useEffect, useRef } from "react";
import { useParams } from "react-router-dom";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import Cookies from "js-cookie";
import axios from "axios";
import "./ChatComponent.css";
import TopBar from "./TopBar";
import BottomBar from "./BottomBar";

const DirectMessageComponent = () => {
    const { dmId } = useParams(); // Get the conversation ID from URL parameters
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState("");
    const [usernames, setUsernames] = useState({});
    const [conversation, setConversation] = useState(null);
    const stompClientRef = useRef(null);
    const messagesEndRef = useRef(null);

    const userId = Cookies.get("userid");

    // Fetch usernames for the provided user IDs
    const fetchUsernames = async (userIds) => {
        try {
            if (!userIds) return;
            const usernameMap = { ...usernames };
            const fetchPromises = userIds
                .filter((id) => !usernameMap[id])
                .map(async (id) => {
                    const response = await axios.get(`http://localhost:8080/user/api/users/specific/${id}`);
                    return { id, username: response.data.username };
                });

            const results = await Promise.all(fetchPromises);
            results.forEach(({ id, username }) => {
                usernameMap[id] = username;
            });

            setUsernames(usernameMap);
        } catch (error) {
            console.error("Error fetching usernames:", error);
        }
    };

    // Fetch conversation details
    const fetchConversationDetails = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/communication/api/conversations/${dmId}/details`);
            setConversation(response.data);

            const allUserIds = response.data?.userIds || [];
            fetchUsernames(allUserIds);
        } catch (error) {
            console.error("Error fetching conversation details:", error);
        }
    };

    // Fetch messages for the conversation
    const fetchMessages = async () => {
        try {
            const response = await axios.get(
                `http://localhost:8080/communication/api/conversations/${dmId}/messages`
            );
            setMessages(response.data);
        } catch (error) {
            console.error("Error fetching messages:", error);
        }
    };

    // Connect to WebSocket for real-time chat
    const connectWebSocket = () => {
        if (!dmId) return;

        const socket = new SockJS("http://localhost:8085/ws");
        const stompClient = new Client({
            webSocketFactory: () => socket,
            debug: (str) => console.log(str),
            onConnect: () => {
                console.log("Connected to WebSocket");

                stompClient.subscribe(`/topic/conversations/${dmId}`, (message) => {
                    const receivedMessage = JSON.parse(message.body);
                    console.log("Received message:", receivedMessage);

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
                conversationId: dmId,
                senderId: userId,
                content: newMessage,
            };

            stompClient.publish({
                destination: `/api/sendMessage/${dmId}`,
                body: JSON.stringify(messageDTO),
            });

            console.log("Message sent:", messageDTO);
            setNewMessage(""); // Clear input only after sending
        } else {
            console.error("Cannot send message: WebSocket is not connected or no conversation selected.");
        }
    };

    // Scroll to the bottom of the chat
    const scrollToBottom = () => {
        if (messagesEndRef.current) {
            messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
        }
    };

    useEffect(() => {
        fetchConversationDetails();
        fetchMessages();
    }, [dmId]);

    useEffect(() => {
        connectWebSocket();
        return () => {
            if (stompClientRef.current) {
                stompClientRef.current.deactivate();
            }
        };
    }, [dmId]);

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    return (
        <div className="dm-content">
            <TopBar />
        <div className="chat-section">
            <h2 className="chat-header">Direct Messages</h2>
            {conversation ? (
                <div className="chat-messages-section">
                    <h3 className="chat-with">
                        Chat with{" "}
                        {conversation.userIds?.filter((id) => String(id) !== String(userId))
                            .map((id) => usernames[id] || `User ${id}`)
                            .join(", ") || "Loading..."}
                    </h3>
                    <div className="chat-messages">
                        {messages.length === 0 ? (
                            <p>No messages yet.</p>
                        ) : (
                            messages.map((msg) => (
                                <div
                                    key={msg.id}
                                    className={`chat-message ${
                                        String(msg.senderId) === String(userId)
                                            ? "chat-message-right"
                                            : "chat-message-left"
                                    }`}
                                >
                                    <strong>
                                        {String(msg.senderId) === String(userId)
                                            ? "You"
                                            : usernames[msg.senderId] || `User ${msg.senderId}`}
                                        :
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
                            className="chat-input-box"
                            onKeyDown={(e) => {
                                if (e.key === "Enter") {
                                    sendMessage();
                                }
                            }}
                        />
                        <button onClick={sendMessage} className="chat-send-button">
                            Send
                        </button>
                    </div>
                </div>
            ) : (
                <p>Loading conversation...</p>
            )}
        </div>
            <BottomBar />
        </div>
    );
};

export default DirectMessageComponent;
