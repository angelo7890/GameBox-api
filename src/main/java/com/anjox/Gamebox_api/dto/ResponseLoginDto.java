package com.anjox.Gamebox_api.dto;

public record ResponseLoginDto(
        Long id,
        String username,
        String email,
        ResponseJwtTokensDto tokens

) {
}
