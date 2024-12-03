import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Login from './authentication/login'; 
import Signup from './authentication/signup'; 
import DashboardLayoutBranding from './home/dashboard'; // Import your Dashboard layout
import Notifications from './commons/notifications'; 
import './App.css'; 

class App extends React.Component {
    render() {
        const username = localStorage.getItem('name'); 
        return (
            <div className="App">
                <Router>
                    <div>
                        <Routes>
                            <Route
                                exact
                                path='/login'
                                element={<Login />} // Renders the Login component
                            />

                            <Route
                                exact
                                path='/signup'
                                element={<Signup />} // Renders the Signup component
                            />

                            <Route path="/*" element={<DashboardLayoutBranding />} />
                        </Routes>
                    </div>
                    {username && <Notifications username={username} />} {/* Only show notifications if user is logged in */}
                </Router>
            </div>
        );
    }
}

export default App;
