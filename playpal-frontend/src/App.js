import logo from './logo.svg';
import './App.css';
import LoginComponent from './components/LoginComponent';
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Homepage from "./components/Homepage";
import ProfileComponent from "./components/ProfileComponent";
import PostPage from "./components/PostPage";
import ChatComponent from "./components/ChatComponent";

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/home" element={<Homepage />} />
                <Route path="/" element={<LoginComponent />} />
                <Route path="/profile/:userId" element={<ProfileComponent />} />
                <Route path="/posts" element={<PostPage />} />
                <Route path="/chat" element={<ChatComponent />} />
            </Routes>
        </Router>
    );
};

export default App;
