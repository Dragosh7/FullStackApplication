package com.example.chatmicroservice.controllers;

import com.example.chatmicroservice.dtos.ChatMessagesDTO;
import com.example.chatmicroservice.dtos.MessageDTO;
import com.example.chatmicroservice.dtos.TypingDTO;
import com.example.chatmicroservice.dtos.builders.MessageBuilder;
import com.example.chatmicroservice.entities.Message;
import com.example.chatmicroservice.entities.Chat;
import com.example.chatmicroservice.repositories.ChatRepository;
import com.example.chatmicroservice.services.ChatService;
import com.example.chatmicroservice.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chats")
public class ChatController {


    private final ChatService chatService;
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRepository chatRepository;


    public ChatController(ChatService chatService, MessageService messageService, SimpMessagingTemplate messagingTemplate, ChatRepository chatRepository) {
        this.chatService = chatService;
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
        this.chatRepository = chatRepository;
    }

    @MessageMapping("/sendMessage")
    public void sendMessage(MessageDTO message) {
        System.out.println("Received WebSocket Message: " + message.getMessageContent());

        Message savedMessage = chatService.saveMessage(message);

        String topic = "/topic/chat/" + message.getChatId();
        messagingTemplate.convertAndSend(topic, MessageBuilder.toDTO(savedMessage));
    }

    @MessageMapping("/readMessage")
    public void readMessage(UUID chatId) {
        //System.out.println("Received Message: " + chatId);
        String topic = "/topic/seen/" + chatId;
        messagingTemplate.convertAndSend(topic, "seen");
    }

    @MessageMapping("/typing")
    public void sendTypingNotification(TypingDTO payload) {
        System.out.println(payload.getChatId());
        String topic = "/topic/typing/" + payload.getChatId();

        messagingTemplate.convertAndSend(topic, payload.getSender());
    }


//    @PostMapping("/{chatId}/messages")
//    public ResponseEntity<Message> createMessage(@PathVariable UUID chatId, @RequestBody Message message) {
//        Message savedMessage = chatService.saveMessage(message);
//
//        messagingTemplate.convertAndSend("/topic/chat/" + chatId, savedMessage);
//
//        return ResponseEntity.ok(savedMessage);
//    }


    @GetMapping("/{userId}")
    public ResponseEntity<List<Chat>> getChatsForUser(@PathVariable UUID userId) {
        List<Chat> chats = chatService.getChatsForUser(userId);
        return ResponseEntity.ok(chats);
    }

    @GetMapping("/{chatId}/messages/{userId}")
    public ResponseEntity<List<ChatMessagesDTO>> getMessagesForChat(@PathVariable UUID chatId,@PathVariable UUID userId) {
        List<ChatMessagesDTO> messages = messageService.getMessagesForChat(chatId,userId);

        boolean hasUnreadMessage = messages.stream()
                .anyMatch(message -> !message.isReadStatus());
        if (hasUnreadMessage) {
            String topic = "/topic/seen/" + chatId;
            messagingTemplate.convertAndSend(topic, ("seen"));
        }
        else {
            System.out.println("All messages are read.");
        }
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/")
    public ResponseEntity<Chat> createChat(@RequestParam String senderName, @RequestParam String receiverName) {
        Chat chat = chatService.createChat(senderName, receiverName);
        return ResponseEntity.ok(chat);
    }
}
