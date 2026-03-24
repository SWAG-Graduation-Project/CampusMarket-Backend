package com.campusmarket.backend.domain.category.service;

import com.campusmarket.backend.domain.category.dto.response.MajorCategoryListResDto;
import com.campusmarket.backend.domain.category.dto.response.SubCategoryListResDto;
import com.campusmarket.backend.domain.category.entity.MajorCategory;
import com.campusmarket.backend.domain.category.entity.SubCategory;
import com.campusmarket.backend.domain.category.exception.CategoryErrorCode;
import com.campusmarket.backend.domain.category.exception.CategoryException;
import com.campusmarket.backend.domain.category.mapper.CategoryMapper;
import com.campusmarket.backend.domain.category.repository.MajorCategoryRepository;
import com.campusmarket.backend.domain.category.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final MajorCategoryRepository majorCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final CategoryMapper categoryMapper;

    public MajorCategoryListResDto getMajorCategories(){
        List<MajorCategory> majorCategories = majorCategoryRepository.findAllByOrderBySortOrderAsc();

        return categoryMapper.toMajorCategoryListResDto(majorCategories);
    }

    public SubCategoryListResDto getSubCategories(Long majorCategoryId) {
        if (majorCategoryId == null) {
            throw new CategoryException(CategoryErrorCode.MAJOR_CATEGORY_NOT_FOUND);
        }

        MajorCategory majorCategory = majorCategoryRepository.findById(majorCategoryId)
                .orElseThrow(() -> new CategoryException(CategoryErrorCode.MAJOR_CATEGORY_NOT_FOUND));

        List<SubCategory> subCategories = subCategoryRepository
                .findAllByMajorCategory_IdOrderBySortOrderAsc(majorCategory.getId());

        return categoryMapper.toSubCategoryListResDto(majorCategory.getId(), subCategories);
    }
}
