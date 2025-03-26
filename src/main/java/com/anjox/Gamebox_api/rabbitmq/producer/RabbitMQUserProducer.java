package com.anjox.Gamebox_api.rabbitmq.producer;


import com.anjox.Gamebox_api.dto.RabbitMQActivationAccountDto;
import com.anjox.Gamebox_api.dto.RabbitMQResetPasswordDto;
import com.anjox.Gamebox_api.security.components.UserRequestAuthorizationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQUserProducer {

    private final RabbitTemplate rabbitTemplate;
    private final Logger logger = LoggerFactory.getLogger(RabbitMQUserProducer.class);

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
        logger.info("Mandando mensagem para fila de ativaçao de conta no RabbitMq");
        rabbitTemplate.convertAndSend(exchangeName, activateAccountQueueName, activationAccountDto);
    }

    public void sendResetPasswordQueue(RabbitMQResetPasswordDto resetPasswordDto) {
        logger.info("Mandado mensagem para fila de refresh password no RabbitMq");
        rabbitTemplate.convertAndSend(exchangeName, resetPasswordQueueName, resetPasswordDto);
    }

}

