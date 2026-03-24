package com.campusmarket.backend.domain.category.dto.response;

import java.util.List;

public record MajorCategoryListResDto(
        List<MajorCategoryItemResDto> majorCategories
) {
    public static MajorCategoryListResDto of(List<MajorCategoryItemResDto> majorCategories) {
        return new MajorCategoryListResDto(majorCategories);
    }
}
