package com.campusmarket.backend.domain.product.dto.response;

import java.time.LocalDateTime;

public record ProductSellerResDto(
        Long sellerId,
        String nickname,
        String profileImageUrl,
        LocalDateTime storeStartedAt,
        Integer saleCount
) {
    public static ProductSellerResDto of(
            Long sellerId,
            String nickname,
            String profileImageUrl,
            LocalDateTime storeStartedAt,
            Integer saleCount
    ) {
        return new ProductSellerResDto(
                sellerId,
                nickname,
                profileImageUrl,
                storeStartedAt,
                saleCount
        );
    }
}