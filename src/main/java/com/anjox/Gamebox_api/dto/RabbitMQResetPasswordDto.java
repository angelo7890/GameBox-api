package com.anjox.Gamebox_api.dto;

public record RabbitMQResetPasswordDto(

        String username,

        String emailTo,

        String encryptedPassword
) {
}
