package com.anjox.Gamebox_api.dto;

import jakarta.validation.constraints.NotBlank;

public record RequestUpdatePictureDto(

        @NotBlank(message = "A URL não pode ser vazia")
        String url,

        @NotBlank(message = "O pictureId não pode ser vazio")
        String pictureId
) {
}
