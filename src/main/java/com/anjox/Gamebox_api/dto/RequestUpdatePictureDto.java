package com.anjox.Gamebox_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RequestUpdatePictureDto(

        @NotNull(message = "O gameId não pode ser nulo")
        Long gameId,

        @NotBlank(message = "A URL não pode ser vazia")
        String url,

        @NotBlank(message = "O pictureId não pode ser vazio")
        String pictureId
) {
}
