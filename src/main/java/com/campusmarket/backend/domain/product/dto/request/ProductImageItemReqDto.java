package com.campusmarket.backend.domain.product.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProductImageItemReqDto(

        @NotBlank(message = "이미지 URL은 필수입니다.")
        String imageUrl
) {
}