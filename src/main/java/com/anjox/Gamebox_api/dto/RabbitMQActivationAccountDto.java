package com.anjox.Gamebox_api.dto;

public record RabbitMQActivationAccountDto(

        String username,

        String emailTo,

        String accountActivationToken

) {
}
