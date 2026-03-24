package com.campusmarket.backend.domain.category.dto.response;

public record SubCategoryItemResDto(
        Long subCategoryId,
        String name,
        String iconUrl,
        Integer sortOrder
) {
    public static SubCategoryItemResDto of(
            Long subCategoryId,
            String name,
            String iconUrl,
            Integer sortOrder
    ) {
        return new SubCategoryItemResDto(
                subCategoryId,
                name,
                iconUrl,
                sortOrder
        );
    }
}
