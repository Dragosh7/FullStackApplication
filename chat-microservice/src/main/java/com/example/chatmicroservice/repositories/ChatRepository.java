package com.example.chatmicroservice.repositories;

import com.example.chatmicroservice.entities.Chat;
import com.example.chatmicroservice.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
    Optional<Chat> findBySenderAndReceiver(Person senderId, Person receiverId);
    List<Chat> findAllBySender(Person sender);
    List<Chat> findAllByReceiver(Person receiver);
}
