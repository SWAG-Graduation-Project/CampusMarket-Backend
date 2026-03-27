package com.campusmarket.backend.domain.product.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProductRedraftReqDto(
        @NotEmpty(message = "tempImageIds는 최소 1개 이상이어야 합니다.")
        @Size(max = 5, message = "tempImageIds는 최대 5개까지 가능합니다.")
        List<Long> tempImageIds,

        String productName,
        String color,
        String productCondition,
        String description,

        Boolean keepProductName,
        Boolean keepColor,
        Boolean keepProductCondition,
        Boolean keepDescription
) {
}