package com.example.chatmicroservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Chat implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Person sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Person receiver;

    @Column(name = "last_activity", nullable = false)
    private LocalDateTime lastActivity;
}
