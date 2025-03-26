package com.anjox.Gamebox_api.dto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RequestUpdateGameDto(

        @NotEmpty String title,

        @NotEmpty String description,

        @NotEmpty String genre,

        @NotNull BigDecimal price

) {
}
