package com.anjox.Gamebox_api.dto;

import com.anjox.Gamebox_api.enums.UserEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RequestRegisterUserDto(

        @NotEmpty(message = "O nome não pode ser vazio")
        String username,

        @NotEmpty(message = "O e-mail não pode ser vazio")
        @Email(message = "O e-mail deve ser válido")
        String email,

        @NotEmpty(message = "A senha não pode ser vazia")
        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
        String password,

        @NotNull(message = "O tipo de usuário não pode ser nulo")
        UserEnum type
) {
}
