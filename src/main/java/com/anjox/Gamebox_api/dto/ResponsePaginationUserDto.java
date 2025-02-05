package com.anjox.Gamebox_api.dto;

import java.util.List;

public record ResponsePaginationUserDto(

        List<ResponseUserDto> content,

        int totalPages,

        long totalElements,

        int pageSize,

        int currentPage
) {
}
