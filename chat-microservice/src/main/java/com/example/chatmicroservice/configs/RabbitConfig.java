package com.example.chatmicroservice.configs;

import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {

    public static final String USER_CHAT_QUEUE = "user-chat-queue";

    @Bean
    public Queue userChangeQueue() {
        return new Queue(USER_CHAT_QUEUE, true);
    }

//    @RabbitListener(queues = DEVICE_CHANGE_QUEUE)  // Listening on the device-change-queue
//    public void receiveDeviceChangeMessage(String message) {
//        // Logic to process the device change message
//        // You will update or create the device here
//        System.out.println("Received message: " + message);
//    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter());
        return template;
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
