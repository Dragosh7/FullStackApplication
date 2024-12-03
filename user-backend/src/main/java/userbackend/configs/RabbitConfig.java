package userbackend.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    public static final String USER_CHANGE_QUEUE = "user-change-queue";
    public static final String USER_CHANGE_EXCHANGE = "user-change-exchange";

    @Bean
    public Queue userChangeQueue() {
        return new Queue(USER_CHANGE_QUEUE, true);
    }

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
//    @Bean
//    public DirectExchange deviceChangeExchange() {
//        return new DirectExchange(DEVICE_CHANGE_EXCHANGE, true, false);
//    }
//
//    @Bean
//    public Binding deviceChangeBinding() {
//        // Bind the queue to the direct exchange with the routing key
//        return BindingBuilder.bind(deviceChangeQueue())
//                .to(deviceChangeExchange())
//                .with(DEVICE_CHANGE_QUEUE);  // Using the queue name as the routing key
//    }
}
