package com.anjox.Gamebox_api.dto;

public record ResponseJwtTokensDto(
        String accessToken,
        String refreshToken
) {
}
