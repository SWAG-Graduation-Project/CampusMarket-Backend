package com.campusmarket.backend.domain.product.dto.response;

public record ProductCreateResDto(
        Long productId
) {
    public static ProductCreateResDto of(Long productId) {
        return new ProductCreateResDto(productId);
    }
}