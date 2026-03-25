package com.campusmarket.backend.domain.product.dto.response;

public record ProductPageInfoResDto(
        Integer page,
        Integer size,
        Long totalElements,
        Integer totalPages,
        Boolean hasNext
) {
    public static ProductPageInfoResDto of(
            Integer page,
            Integer size,
            Long totalElements,
            Integer totalPages,
            Boolean hasNext
    ) {
        return new ProductPageInfoResDto(
                page,
                size,
                totalElements,
                totalPages,
                hasNext
        );
    }
}