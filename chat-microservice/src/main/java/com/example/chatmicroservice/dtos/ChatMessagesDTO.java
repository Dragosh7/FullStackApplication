package com.example.chatmicroservice.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessagesDTO {
    private String senderId;
    private String messageContent;
    private LocalDateTime timestamp;
    private boolean readStatus;
}
