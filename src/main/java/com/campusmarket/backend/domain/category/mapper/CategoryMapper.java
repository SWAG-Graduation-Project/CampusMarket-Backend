package com.campusmarket.backend.domain.category.mapper;

import com.campusmarket.backend.domain.category.dto.response.MajorCategoryItemResDto;
import com.campusmarket.backend.domain.category.dto.response.MajorCategoryListResDto;
import com.campusmarket.backend.domain.category.dto.response.SubCategoryItemResDto;
import com.campusmarket.backend.domain.category.dto.response.SubCategoryListResDto;
import com.campusmarket.backend.domain.category.entity.MajorCategory;
import com.campusmarket.backend.domain.category.entity.SubCategory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryMapper {

    public MajorCategoryItemResDto toMajorCategoryItemResDto(MajorCategory majorCategory) {
        return MajorCategoryItemResDto.of(
                majorCategory.getId(),
                majorCategory.getName(),
                majorCategory.getIconUrl(),
                majorCategory.getSortOrder()
        );
    }

    public MajorCategoryListResDto toMajorCategoryListResDto(List<MajorCategory> majorCategories) {
        List<MajorCategoryItemResDto> majorCategoryItems = majorCategories.stream()
                .map(this::toMajorCategoryItemResDto)
                .toList();

        return MajorCategoryListResDto.of(majorCategoryItems);
    }

    public SubCategoryItemResDto toSubCategoryItemResDto(SubCategory subCategory) {
        return SubCategoryItemResDto.of(
                subCategory.getId(),
                subCategory.getName(),
                subCategory.getIconUrl(),
                subCategory.getSortOrder()
        );
    }

    public SubCategoryListResDto toSubCategoryListResDto(
            Long majorCategoryId,
            List<SubCategory> subCategories
    ) {
        List<SubCategoryItemResDto> subCategoryItems = subCategories.stream()
                .map(this::toSubCategoryItemResDto)
                .toList();

        return SubCategoryListResDto.of(majorCategoryId, subCategoryItems);
    }
}
