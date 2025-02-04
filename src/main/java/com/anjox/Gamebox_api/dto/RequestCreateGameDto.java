package com.anjox.Gamebox_api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RequestCreateGameDto(

       @NotNull Long userId,

       @NotNull @NotEmpty String title,

       @NotNull @NotEmpty String description,

       @NotNull @NotEmpty String genre,

       @NotNull BigDecimal price,

                String imageUrl
) {
}
