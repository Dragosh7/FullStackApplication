package com.example.monitoringbackend.services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendLimitExceededNotification(String username, String deviceName) {
        // Send the notification to the frontend (client-side subscribed to "/topic/notifications")
        String message = username + ": You exceeded the hourly limit for the device " + deviceName;
        System.out.println(message);
        messagingTemplate.convertAndSend("/topic/notifications", message);
    }
}
