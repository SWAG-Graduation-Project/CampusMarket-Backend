package com.campusmarket.backend.domain.product.dto.request;

public record SearchProductsReqDto(
        String keyword,
        Long majorCategoryId,
        Long subCategoryId,
        String sort,
        Integer page,
        Integer size
) {
}
