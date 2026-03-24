package com.campusmarket.backend.domain.category.dto.response;

public record MajorCategoryItemResDto(
        Long majorCategoryId,
        String name,
        String iconUrl,
        Integer sortOrder
) {
    public static MajorCategoryItemResDto of(
            Long majorCategoryId,
            String name,
            String iconUrl,
            Integer sortOrder
    ) {
        return new MajorCategoryItemResDto(
                majorCategoryId,
                name,
                iconUrl,
                sortOrder
        );
    }
}
