package com.campusmarket.backend.domain.product.dto.request;

import com.campusmarket.backend.domain.product.entity.ProductCondition;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductUpdateReqDto(

        @NotBlank
        @Size(max = 200, message = "상품명은 200자 이하로 입력해주세요.")
        String name,

        @Size(max = 200, message = "브랜드는 200자 이하로 입력해주세요.")
        String brand,

        @Size(max = 50, message = "색상은 50자 이하로 입력해주세요.")
        String color,

        @NotNull
        ProductCondition productCondition,

        @NotBlank
        String description,

        @NotNull
        @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
        Integer price,

        @NotNull
        Boolean isFree
) {
}