package com.campusmarket.backend.domain.product.dto.response;

public record BackgroundRemovalItemResDto(
        Long tempImageId,
        String backgroundRemovedImageUrl
) {
    public static BackgroundRemovalItemResDto of(
            Long tempImageId,
            String backgroundRemovedImageUrl
    ) {
        return new BackgroundRemovalItemResDto(tempImageId, backgroundRemovedImageUrl);
    }
}