package com.anjox.Gamebox_api.rabbitmq.producer;


import com.anjox.Gamebox_api.dto.RabbitMQActivationAccountDto;
import com.anjox.Gamebox_api.dto.RabbitMQResetPasswordDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQUserProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.template.exchange}")
    private String exchangeName;

    @Value("${spring.rabbitmq.reset-password.queue}")
    private String resetPasswordQueueName;

    @Value("${spring.rabbitmq.activate-account.queue}")
    private String activateAccountQueueName;

    public RabbitMQUserProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendActivationAccountQueue(RabbitMQActivationAccountDto activationAccountDto) {
        rabbitTemplate.convertAndSend(exchangeName, activateAccountQueueName, activationAccountDto);
    }

    public void sendResetPasswordQueue(RabbitMQResetPasswordDto resetPasswordDto) {
        rabbitTemplate.convertAndSend(exchangeName, resetPasswordQueueName, resetPasswordDto);
    }

}

