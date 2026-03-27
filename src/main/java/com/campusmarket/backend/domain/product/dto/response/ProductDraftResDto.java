package com.campusmarket.backend.domain.product.dto.response;

public record ProductDraftResDto(
        Long majorCategoryId,
        String majorCategoryName,
        Long subCategoryId,
        String subCategoryName,
        String productName,
        String color,
        String productCondition,
        String description
) {
    public static ProductDraftResDto of(
            Long majorCategoryId,
            String majorCategoryName,
            Long subCategoryId,
            String subCategoryName,
            String productName,
            String color,
            String productCondition,
            String description
    ) {
        return new ProductDraftResDto(
                majorCategoryId,
                majorCategoryName,
                subCategoryId,
                subCategoryName,
                productName,
                color,
                productCondition,
                description
        );
    }
}