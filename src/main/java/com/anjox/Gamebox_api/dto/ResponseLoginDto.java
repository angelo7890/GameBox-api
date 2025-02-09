package com.anjox.Gamebox_api.dto;

public record ResponseLoginDto(
        String token,
        String refreshToken
) {
}
