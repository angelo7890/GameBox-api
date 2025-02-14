package com.anjox.Gamebox_api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RequestCreateGameDto(

        @NotNull(message = "O userId não pode ser nulo")
        Long userId,

        @NotNull(message = "O título não pode ser nulo")
        @NotEmpty(message = "O título não pode estar vazio")
        String title,

        @NotNull(message = "A descrição não pode ser nula")
        @NotEmpty(message = "A descrição não pode estar vazia")
        String description,

        @NotNull(message = "O gênero não pode ser nulo")
        @NotEmpty(message = "O gênero não pode estar vazio")
        String genre,

        @NotNull(message = "O preço não pode ser nulo")
        @Positive(message = "O preço deve ser um valor positivo")
        BigDecimal price,

        String imageUrl,
        String imageId
) {
}
