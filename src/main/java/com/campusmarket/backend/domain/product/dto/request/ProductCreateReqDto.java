package com.campusmarket.backend.domain.product.dto.request;

import com.campusmarket.backend.domain.product.entity.ProductCondition;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProductCreateReqDto(

        @NotNull(message = "대카테고리 ID는 필수입니다.")
        Long majorCategoryId,

        @NotNull(message = "소카테고리 ID는 필수입니다.")
        Long subCategoryId,

        @NotBlank(message = "상품명은 필수입니다.")
        String name,

        String brand,

        String color,

        @NotNull(message = "상품 상태는 필수입니다.")
        ProductCondition productCondition,

        @NotBlank(message = "상품 설명은 필수입니다.")
        String description,

        @NotNull(message = "가격은 필수입니다.")
        @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
        Integer price,

        @NotNull(message = "무료나눔 여부는 필수입니다.")
        Boolean isFree,

        @Valid
        List<ProductImageItemReqDto> images
) {
}