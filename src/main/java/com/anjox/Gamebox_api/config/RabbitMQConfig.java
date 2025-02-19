package com.anjox.Gamebox_api.config;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.template.exchange}")
    private String exchangeName;

    @Value("${spring.rabbitmq.reset-password.queue}")
    private String resetPasswordQueueName;

    @Value("${spring.rabbitmq.activate-account.queue}")
    private String activateAccountQueueName;

    @Bean
    public TopicExchange userActionsExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Queue resetPasswordQueue() {
        return new Queue(resetPasswordQueueName, false);
    }

    @Bean
    public Queue activateAccountQueue() {
        return new Queue(activateAccountQueueName, false);
    }

    @Bean
    public Binding bindingResetPassword(Queue resetPasswordQueue, TopicExchange userActionsExchange) {
        return BindingBuilder.bind(resetPasswordQueue)
                .to(userActionsExchange)
                .with("reset-password");
    }

    @Bean
    public Binding bindingActivateAccount(Queue activateAccountQueue, TopicExchange userActionsExchange) {
        return BindingBuilder.bind(activateAccountQueue)
                .to(userActionsExchange)
                .with("account-activation");
    }
}
