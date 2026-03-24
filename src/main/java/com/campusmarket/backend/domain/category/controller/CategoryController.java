package com.campusmarket.backend.domain.category.controller;

import com.campusmarket.backend.domain.category.dto.response.MajorCategoryListResDto;
import com.campusmarket.backend.domain.category.dto.response.SubCategoryListResDto;
import com.campusmarket.backend.domain.category.service.CategoryService;
import com.campusmarket.backend.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryController implements CategoryControllerDocs {

    private final CategoryService categoryService;

    @Override
    @GetMapping("/major")
    public ApiResponse<MajorCategoryListResDto> getMajorCategories() {
        return ApiResponse.success(categoryService.getMajorCategories());
    }

    @Override
    @GetMapping("/major/{majorCategoryId}/sub")
    public ApiResponse<SubCategoryListResDto> getSubCategories(@PathVariable Long majorCategoryId) {
        return ApiResponse.success(categoryService.getSubCategories(majorCategoryId));
    }
}