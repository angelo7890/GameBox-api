package com.anjox.Gamebox_api.service;


import com.anjox.Gamebox_api.dto.RabbitMQActivationAccountDto;
import com.anjox.Gamebox_api.dto.RabbitMQResetPasswordDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQService {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value("${spring.rabbitmq.template.exchange}")
    private String exchangeName;

    @Value("${spring.rabbitmq.reset-password.queue}")
    private String resetPasswordQueueName;

    @Value("${spring.rabbitmq.activate-account.queue}")
    private String activateAccountQueueName;


    public void sendResetPasswordQueue(RabbitMQResetPasswordDto dto) {
        rabbitTemplate.convertAndSend(exchangeName, resetPasswordQueueName, dto);
    }

    public void sendActivateAccountQueue(RabbitMQActivationAccountDto dto) {
        rabbitTemplate.convertAndSend(exchangeName, activateAccountQueueName, dto);
    }

}
