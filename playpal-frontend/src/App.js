import logo from './logo.svg';
import './App.css';
import LoginComponent from './components/LoginComponent';
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Homepage from "./components/Homepage";
import ProfileComponent from "./components/ProfileComponent";

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/home" element={<Homepage />} />
                <Route path="/" element={<LoginComponent />} />
                <Route path="/profile" element={<ProfileComponent />} />
            </Routes>
        </Router>
    );
};

export default App;
