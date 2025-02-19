package com.anjox.Gamebox_api.dto;
import java.math.BigDecimal;

public record RequestUpdateGameDto(

        String title,

        String description,

        String genre,

        BigDecimal price

) {
}
