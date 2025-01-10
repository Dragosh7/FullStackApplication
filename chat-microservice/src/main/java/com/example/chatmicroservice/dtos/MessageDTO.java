package com.example.chatmicroservice.dtos;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private String chatId;
    private String sender;
    private String messageContent;
}
