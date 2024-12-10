import React, { useState, useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import Cookies from "js-cookie";
import "./ChatComponent.css";

const ChatComponent = () => {
    const [conversations, setConversations] = useState([]);
    const [selectedConversation, setSelectedConversation] = useState(null);
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState("");
    const stompClientRef = useRef(null);
    const messagesEndRef = useRef(null);

    const userid = Cookies.get("userid");
    console.log("User ID set in cookies:", userid);

    const addMessage = (newMessage) => {
        setMessages((prevMessages) => {
            const exists = prevMessages.some((msg) => msg.id === newMessage.id);
            if (!exists) {
                return [...prevMessages, newMessage];
            }
            return prevMessages;
        });
    };

    // Scroll to the bottom of the chat
    const scrollToBottom = () => {
        if (messagesEndRef.current) {
            messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
        }
    };

    // Scroll down whenever messages change
    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    useEffect(() => {
        const fetchConversations = async () => {
            if (!userid) {
                console.error("No user ID found in cookies");
                return;
            }

            try {
                const response = await fetch(`http://localhost:8085/api/conversations/${userid}/conversations`);
                if (!response.ok) {
                    throw new Error("Failed to fetch conversations");
                }
                const data = await response.json();
                setConversations(data);

                if (data.length > 0) {
                    setSelectedConversation(data[0]);
                }
            } catch (error) {
                console.error("Error fetching conversations:", error);
            }
        };

        fetchConversations();
    }, [userid]);

    useEffect(() => {
        const fetchMessages = async () => {
            if (!selectedConversation?.id) return;

            try {
                const response = await fetch(`http://localhost:8085/api/conversations/${selectedConversation.id}/messages`);
                if (!response.ok) {
                    throw new Error("Failed to fetch messages");
                }
                const data = await response.json();
                setMessages(data);
            } catch (error) {
                console.error("Error fetching messages:", error);
            }
        };

        fetchMessages();
    }, [selectedConversation]);

    useEffect(() => {
        if (!selectedConversation?.id) return;

        const socket = new SockJS("http://localhost:8085/ws");
        const stompClient = new Client({
            webSocketFactory: () => socket,
            debug: (str) => console.log(str),
            onConnect: () => {
                console.log("Connected to WebSocket");

                stompClient.subscribe(`/topic/conversations/${selectedConversation.id}`, (message) => {
                    const receivedMessage = JSON.parse(message.body);
                    console.log("Received message:", receivedMessage);
                    addMessage(receivedMessage);
                });
            },
        });

        stompClient.activate();
        stompClientRef.current = stompClient;

        return () => {
            stompClient.deactivate();
        };
    }, [selectedConversation]);

    const sendMessage = () => {
        const stompClient = stompClientRef.current;

        if (stompClient && stompClient.connected && selectedConversation) {
            const messageDTO = {
                conversationId: selectedConversation.id,
                senderId: userid,
                content: newMessage,
            };

            stompClient.publish({
                destination: `/api/sendMessage/${selectedConversation.id}`,
                body: JSON.stringify(messageDTO),
            });

            console.log("Message sent:", messageDTO);
            setNewMessage("");
        } else {
            console.error("Cannot send message: WebSocket is not connected or no conversation selected.");
        }
    };

    return (
        <div>
            <h2>Chat</h2>

            {/* Conversations List */}
            <div>
                <h3>Conversations</h3>
                {conversations.length === 0 ? (
                    <p>No conversations available.</p>
                ) : (
                    <ul>
                        {conversations.map((conversation) => (
                            <li
                                key={conversation.id}
                                onClick={() => setSelectedConversation(conversation)}
                                style={{
                                    cursor: "pointer",
                                    textDecoration: selectedConversation?.id === conversation.id ? "underline" : "none",
                                }}
                            >
                                {conversation.groupName || `Conversation ${conversation.id}`}
                            </li>
                        ))}
                    </ul>
                )}
            </div>

            {/* Messages Section */}
            {selectedConversation && (
                <div>
                    <h3>Messages for {selectedConversation.groupName || `Conversation ${selectedConversation.id}`}</h3>
                    <div
                        className="chat-messages"
                        style={{
                            border: "1px solid #ddd",
                            height: "300px",
                            overflowY: "auto",
                            padding: "10px",
                        }}
                    >
                        {messages.length === 0 ? (
                            <p>No messages yet.</p>
                        ) : (
                            messages.map((msg, index) => (
                                <div
                                    key={index}
                                    className={`chat-message ${
                                        String(msg.senderId) === String(userid) ? "chat-message-right" : "chat-message-left"
                                    }`}
                                >
                                    <strong>{String(msg.senderId) === String(userid) ? "You" : `User ${msg.senderId}`}:</strong> {msg.content}
                                </div>
                            ))
                        )}
                        {/* Invisible div to ensure scrolling to the bottom */}
                        <div ref={messagesEndRef} />
                    </div>
                    <div
                        className="chat-input"
                        style={{ display: "flex", marginTop: "10px" }}
                    >
                        <input
                            type="text"
                            value={newMessage}
                            onChange={(e) => setNewMessage(e.target.value)}
                            placeholder="Type a message..."
                            style={{ flex: 1, padding: "5px" }}
                            onKeyDown={(e) => {
                                if (e.key === "Enter") {
                                    sendMessage(); // Call sendMessage when Enter is pressed
                                }
                            }}
                        />
                        <button
                            onClick={sendMessage}
                            style={{ marginLeft: "5px", padding: "5px 10px" }}
                        >
                            Send
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ChatComponent;
