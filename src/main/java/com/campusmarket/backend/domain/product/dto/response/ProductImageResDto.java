package com.campusmarket.backend.domain.product.dto.response;

public record ProductImageResDto(
        Long productImageId,
        String imageUrl,
        String originalImageUrl,
        Boolean backgroundRemoved,
        Integer displayOrder
) {
    public static ProductImageResDto of(
            Long productImageId,
            String imageUrl,
            String originalImageUrl,
            Boolean backgroundRemoved,
            Integer displayOrder
    ) {
        return new ProductImageResDto(
                productImageId,
                imageUrl,
                originalImageUrl,
                backgroundRemoved,
                displayOrder
        );
    }
}