package com.campusmarket.backend.domain.product.dto.response;

public record ProductViewIncreaseResDto(
        Long productId,
        Integer viewCount
) {
    public static ProductViewIncreaseResDto of(Long productId, Integer viewCount) {
        return new ProductViewIncreaseResDto(productId, viewCount);
    }
}