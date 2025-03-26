package com.anjox.Gamebox_api.dto;

import jakarta.validation.constraints.NotEmpty;

public record RequestUpdateUserDto(

       String username,

       String password
) {
}