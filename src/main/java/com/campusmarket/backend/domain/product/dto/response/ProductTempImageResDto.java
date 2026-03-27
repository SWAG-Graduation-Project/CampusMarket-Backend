package com.campusmarket.backend.domain.product.dto.response;

public record ProductTempImageResDto(
        Long tempImageId,
        String originalImageUrl,
        String backgroundRemovedImageUrl,
        Boolean backgroundRemoved,
        Integer displayOrder
) {
    public static ProductTempImageResDto of(
            Long tempImageId,
            String originalImageUrl,
            String backgroundRemovedImageUrl,
            Boolean backgroundRemoved,
            Integer displayOrder
    ) {
        return new ProductTempImageResDto(
                tempImageId,
                originalImageUrl,
                backgroundRemovedImageUrl,
                backgroundRemoved,
                displayOrder
        );
    }
}