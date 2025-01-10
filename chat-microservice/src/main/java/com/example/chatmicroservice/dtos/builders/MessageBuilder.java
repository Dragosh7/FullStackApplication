package com.example.chatmicroservice.dtos.builders;

import com.example.chatmicroservice.dtos.MessageDTO;
import com.example.chatmicroservice.entities.Message;

public class MessageBuilder {
    public static MessageDTO toDTO(Message message) {
        return new MessageDTO(message.getChat().getId().toString(),
                message.getSender().getId().toString(),
                message.getMessageContent());
    }
}
