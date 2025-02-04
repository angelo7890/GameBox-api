package com.anjox.Gamebox_api.dto;

import java.util.List;

public record ResponsePaginationGameDto(

        List<ResponseGameDto> content,

        Integer totalPages,

        Long totalElements,

        Integer pageSize,

        Integer currentPage
) {
}
