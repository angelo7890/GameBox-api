package com.anjox.Gamebox_api.dto;

import com.anjox.Gamebox_api.enums.UserEnum;

public record RequestRegisterUserDto(

        String name,

        String email,

        String password,

        UserEnum type
) {
}
