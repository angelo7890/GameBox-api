package com.anjox.Gamebox_api.dto;

import java.util.List;

public record ResponsePaginationGameDto(

        List<ResponseGameDto> content,

        int totalPages,

        long totalElements,

        int pageSize,

        int currentPage
) {
}
