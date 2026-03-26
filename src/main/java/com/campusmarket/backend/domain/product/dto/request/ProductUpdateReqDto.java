package com.campusmarket.backend.domain.product.dto.request;

import com.campusmarket.backend.domain.product.entity.ProductCondition;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductUpdateReqDto(

        @NotBlank
        String name,

        String brand,

        String color,

        @NotNull
        ProductCondition productCondition,

        @NotBlank
        String description,

        @NotNull
        @Min(0)
        Integer price,

        @NotNull
        Boolean isFree
) {
}