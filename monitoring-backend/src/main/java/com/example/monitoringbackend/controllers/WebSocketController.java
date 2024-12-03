package com.example.monitoringbackend.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/send") // Matches "/app/send" from client
    @SendTo("/topic/messages") // Sends to "/topic/messages"
    public String handleMessage(String message) {
        System.out.println("Received message: " + message);
        return "Server response: " + message;
    }
}
