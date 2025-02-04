package com.anjox.Gamebox_api.dto;

import java.util.List;

public record ResponsePaginationUserDto(

        List<ResponseUserDto> content,

        Integer totalPages,

        Long totalElements,

        Integer pageSize,

        Integer currentPage
) {
}
