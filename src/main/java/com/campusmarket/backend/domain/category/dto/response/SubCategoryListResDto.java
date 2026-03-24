package com.campusmarket.backend.domain.category.dto.response;

import java.util.List;

public record SubCategoryListResDto(
        Long majorCategoryId,
        List<SubCategoryItemResDto> subCategories
) {
    public static SubCategoryListResDto of(
            Long majorCategoryId,
            List<SubCategoryItemResDto> subCategories
    ) {
        return new SubCategoryListResDto(
                majorCategoryId,
                subCategories
        );
    }
}
