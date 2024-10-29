// NotFound.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Typography, CircularProgress } from '@mui/material';

const NotFound = () => {
    const [countdown, setCountdown] = useState(5); // 5 seconds countdown
    const navigate = useNavigate();

    useEffect(() => {
        // Set a countdown timer
        const intervalId = setInterval(() => {
            setCountdown((prev) => prev - 1);
        }, 1000); // Countdown decreases every second

        // Redirect to login after 5 seconds
        const timeoutId = setTimeout(() => {
            navigate('/login');
        }, 5000); // 5 seconds timeout

        // Cleanup the intervals and timeouts
        return () => {
            clearInterval(intervalId);
            clearTimeout(timeoutId);
        };
    }, [navigate]);

    return (
        <Box 
            display="flex" 
            flexDirection="column" 
            justifyContent="center" 
            alignItems="center" 
            height="100vh"
        >
            <Typography variant="h4" color="error" gutterBottom>
                404 - Page Not Found
            </Typography>
            <Typography variant="body1" gutterBottom>
                Redirecting you to the login page in {countdown} seconds...
            </Typography>
            <CircularProgress color="error"/>
        </Box>
    );
};

export default NotFound;
