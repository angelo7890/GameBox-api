package com.anjox.Gamebox_api.dto;

import com.anjox.Gamebox_api.enums.UserEnum;

public record ResponseUserDto(
        Long id,
        String name,
        String email,
        UserEnum type
) {
}
