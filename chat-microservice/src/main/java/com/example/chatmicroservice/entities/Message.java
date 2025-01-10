package com.example.chatmicroservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Person sender;

    @Column(name = "message_content", nullable = false, length = 500)
    private String messageContent;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "read_status", nullable = false)
    private boolean readStatus = false;
}

