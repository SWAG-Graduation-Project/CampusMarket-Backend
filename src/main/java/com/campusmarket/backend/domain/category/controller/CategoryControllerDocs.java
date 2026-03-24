package com.campusmarket.backend.domain.category.controller;

import com.campusmarket.backend.domain.category.dto.response.MajorCategoryListResDto;
import com.campusmarket.backend.domain.category.dto.response.SubCategoryListResDto;
import com.campusmarket.backend.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "Category", description = "카테고리 관련 API")
@RequestMapping("/categories")
public interface CategoryControllerDocs {

    @Operation(summary = "대카테고리 목록 조회", description = "상단 12개 탭에 사용할 대카테고리 목록을 조회합니다.")
    ApiResponse<MajorCategoryListResDto> getMajorCategories();

    @Operation(summary = "소카테고리 목록 조회", description = "선택한 대카테고리에 해당하는 소카테고리 목록을 조회합니다.")
    ApiResponse<SubCategoryListResDto> getSubCategories(
            @Parameter(description = "대카테고리 ID", example = "1")
            @PathVariable Long majorCategoryId
    );
}
