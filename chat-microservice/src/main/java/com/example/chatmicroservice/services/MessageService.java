package com.example.chatmicroservice.services;

import com.example.chatmicroservice.dtos.MessageDTO;
import com.example.chatmicroservice.entities.Message;
import com.example.chatmicroservice.dtos.ChatMessagesDTO;
import com.example.chatmicroservice.repositories.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<ChatMessagesDTO>    getMessagesForChat(UUID chatId, UUID userId) {
        //return messageRepository.findAllByChatId(chatId);
        List<Message> messages = messageRepository.findAllByChatId(chatId);
        //System.out.println(messages);
        messages.stream()
                .filter(message -> !message.isReadStatus() &&
                        !message.getSender().getId().equals(userId))
                .forEach(message -> {
                    message.setReadStatus(true); // Mark as seen
                    messageRepository.save(message);
                });
        // Transform to DTOs
        List<ChatMessagesDTO> messageDTOs = messages.stream()
                .map(message -> ChatMessagesDTO.builder()
                        .senderId(message.getSender().getId().toString())
                        .messageContent(message.getMessageContent())
                        .timestamp(message.getTimestamp())
                        .readStatus(message.isReadStatus())
                        .build())
                .collect(Collectors.toList());
        //System.out.println(messageDTOs);
        // Sort and format the DTOs
        return getSortedAndFormattedMessages(messageDTOs);
    }

    public void markMessagesAsRead(UUID chatId, UUID receiverId) {
        List<Message> unreadMessages = messageRepository.findAllByChatId(chatId);

        for (Message message : unreadMessages) {
            message.setReadStatus(true);
            messageRepository.save(message);
        }
    }

    public List<ChatMessagesDTO> getSortedAndFormattedMessages(List<ChatMessagesDTO> messages) {

        // Sort messages by timestamp and format the time
        return messages.stream()
                .sorted(Comparator.comparing(ChatMessagesDTO::getTimestamp)) // Sort by timestamp
                .map(message -> {
                    message.setMessageContent(message.getMessageContent());
                    return message;
                })
                .collect(Collectors.toList());
    }
}
