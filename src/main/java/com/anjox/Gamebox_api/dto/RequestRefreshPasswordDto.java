package com.anjox.Gamebox_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequestRefreshPasswordDto(
        @NotBlank(message = "o email no pode ser nulo")
        @Email(message = "o email tem que ser valido")
        String email
) {
}
