package com.anjox.Gamebox_api.dto;

import com.anjox.Gamebox_api.enums.UserEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RequestRegisterUserDto(

        @NotNull @NotEmpty  String name,

        @NotNull @NotEmpty  String email,

        @NotNull @NotEmpty String password,

         UserEnum type
) {
}
