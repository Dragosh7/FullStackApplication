import React, { useState, useEffect } from 'react';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Snackbar, Alert } from '@mui/material';

const Notifications = ({ username }) => {
  const [open, setOpen] = useState(false);
  const [message, setMessage] = useState('');
  const [connected, setConnected] = useState(false); // State to track WebSocket connection

  useEffect(() => {
    const socket = new SockJS('http://localhost:8089/ws');  // Connect to WebSocket endpoint
    const stompClient = Stomp.over(socket);

    // Attempt to connect
    stompClient.connect({}, (frame) => {
      console.log('Connected to WebSocket: ', frame);  // Log the connection frame
      setConnected(true);  // Set connected to true when WebSocket connection is successful

      // Subscribe to /topic/notifications
      stompClient.subscribe('/topic/notifications', (message) => {
        // Check if the message contains the username
        console.log('receiveed ', message);  // Log the connection frame

        if (message.body.includes(username)) {
          setMessage(message.body);  // Show the message if it contains the user's name
          setOpen(true);
        }
      });
    }, (error) => {
      console.error('WebSocket connection failed: ', error);
      setConnected(false);  // Set connected to false if WebSocket connection fails
    });

    // Cleanup on unmount
    return () => {
      stompClient.disconnect(() => {
        console.log('Disconnected from WebSocket');
      });
    };
  }, [username]);

  // Close the notification
  const handleClose = () => {
    setOpen(false);
  };

  return (
    <div>
      {/* Notification Popup */}
      <Snackbar open={open} autoHideDuration={6000} onClose={handleClose} anchorOrigin={{ vertical: 'top', horizontal: 'right' }}>
        <Alert onClose={handleClose} severity="warning" sx={{ width: '100%' }}>
          {message}
        </Alert>
      </Snackbar>

      {/* Connection Status */}
      <div>
        {connected ? (
          <p>WebSocket is connected.</p>
        ) : (
          <p>WebSocket is not connected.</p>
        )}
      </div>
    </div>
  );
};

export default Notifications;
