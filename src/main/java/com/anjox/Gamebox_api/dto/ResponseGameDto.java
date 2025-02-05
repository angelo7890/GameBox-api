package com.anjox.Gamebox_api.dto;

import com.anjox.Gamebox_api.enums.UserEnum;

import java.math.BigDecimal;

public record ResponseGameDto(

        Long gameId,

        String title,

        String description,

        String genre,

        BigDecimal price,

        String imageUrl
) {
}
