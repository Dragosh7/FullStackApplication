package com.example.chatmicroservice.repositories;

import com.example.chatmicroservice.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findAllByChatId(UUID chatId);

}
