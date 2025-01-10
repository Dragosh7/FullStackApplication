package com.example.chatmicroservice.dtos;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypingDTO {
    private String chatId;
    private String sender;
}
