package com.example.chatmicroservice.dtos;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReadMessageDTO {
    private String chatId;
    private String receiverId;
}
