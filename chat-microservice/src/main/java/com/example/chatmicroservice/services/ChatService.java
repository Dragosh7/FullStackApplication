package com.example.chatmicroservice.services;

import com.example.chatmicroservice.dtos.MessageDTO;
import com.example.chatmicroservice.entities.Chat;
import com.example.chatmicroservice.entities.Message;
import com.example.chatmicroservice.entities.Person;
import com.example.chatmicroservice.repositories.ChatRepository;
import com.example.chatmicroservice.repositories.MessageRepository;
import com.example.chatmicroservice.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatService {

    private final ChatRepository chatRepository;

    private final PersonRepository personRepository;

    private final MessageRepository messageRepository;


    public ChatService(ChatRepository chatRepository, PersonRepository personRepository, MessageRepository messageRepository) {
        this.chatRepository = chatRepository;
        this.personRepository = personRepository;
        this.messageRepository = messageRepository;
    }

    public Chat findOrCreateChat(UUID senderId, UUID receiverId) {
        Optional<Person> sender = personRepository.findById(senderId);
        Optional<Person> receiver = personRepository.findById(receiverId);

        if(sender.isPresent() && receiver.isPresent()) {
            return chatRepository.findBySenderAndReceiver(sender.get(), receiver.get())
                    .orElseGet(() -> {
                        Chat chat = new Chat();
                        chat.setSender(sender.get());
                        chat.setReceiver(receiver.get());
                        chat.setLastActivity(LocalDateTime.now());
                        return chatRepository.save(chat);
                    });
        }
        return null;
    }

    public Message saveMessage(MessageDTO messageDTO) {
        Message message = new Message();
        Optional<Chat> chat = chatRepository.findById(UUID.fromString(messageDTO.getChatId()));
        if(chat.isPresent()) {
            message.setChat(chat.get());
        }
        Optional<Person> person = personRepository.findById(UUID.fromString(messageDTO.getSender()));
        if(person.isPresent()) {
            message.setSender(person.get());
        }
        message.setMessageContent(messageDTO.getMessageContent());
        message.setTimestamp(LocalDateTime.now());
        message.setReadStatus(false);
        return messageRepository.save(message);
    }


    public List<Chat> getChatsForUser(UUID userId) {
        Optional<Person> user = personRepository.findById(userId);
        if(user.isPresent()) {
            List<Chat> chatsAsSender = chatRepository.findAllBySender(user.get());
            List<Chat> chatsAsReceiver = chatRepository.findAllByReceiver(user.get());
            chatsAsSender.addAll(chatsAsReceiver);
            return chatsAsSender;
        }
    return null;
    }

    public Chat createChat(String senderName, String receiverName) {
        Optional<Person> sender = personRepository.findByName(senderName);
        Optional<Person> receiver = personRepository.findByName(receiverName);

        if (sender.get().getName().equals(receiverName)) {
            return null;
        }

        return findOrCreateChat(sender.get().getId(), receiver.get().getId());
    }
}
