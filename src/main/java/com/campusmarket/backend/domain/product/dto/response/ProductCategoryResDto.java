package com.campusmarket.backend.domain.product.dto.response;

public record ProductCategoryResDto(
        Long majorCategoryId,
        String majorCategoryName,
        Long subCategoryId,
        String subCategoryName
) {
    public static ProductCategoryResDto of(
            Long majorCategoryId,
            String majorCategoryName,
            Long subCategoryId,
            String subCategoryName
    ) {
        return new ProductCategoryResDto(
                majorCategoryId,
                majorCategoryName,
                subCategoryId,
                subCategoryName
        );
    }
}