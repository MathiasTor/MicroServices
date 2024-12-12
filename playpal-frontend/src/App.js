import logo from './logo.svg';
import './App.css';
import LoginComponent from './components/LoginComponent';
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Homepage from "./components/Homepage";
import ProfileComponent from "./components/ProfileComponent";
import PostPage from "./components/PostPage";
import ChatComponent from "./components/ChatComponent";
import LiveSearchComponent from "./components/LiveSearchComponent";
import GroupComponent from "./components/GroupComponent";
import GroupOverviewComponent from "./components/GroupOverviewComponent";
import DirectMessageComponent from "./components/DirectMessageComponent";
import Leaderboard from "./components/Leaderboard";
import LiveSearch from "./components/LiveSearch";

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/home" element={<Homepage />} />
                <Route path="/" element={<LoginComponent />} />
                <Route path="/profile/:userId" element={<ProfileComponent />} />
                <Route path="/posts" element={<PostPage />} />
                <Route path="/chat" element={<ChatComponent />} />
                <Route path={"/live-search"} element={<LiveSearch />} />
                <Route path={"/groups"} element={<GroupComponent />} />
                <Route path="/groups/:groupId" element={<GroupOverviewComponent />} />
                <Route path="/dm/:dmId" element={<DirectMessageComponent />} />
                <Route path="/leaderboard" element={<Leaderboard />} />
            </Routes>
        </Router>
    );
};

export default App;
