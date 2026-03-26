package com.campusmarket.backend.domain.store.dto.response;

import java.time.LocalDateTime;

public record StoreDetailResDto(
        Long sellerId,
        String nickname,
        String profileImageUrl,
        LocalDateTime storeStartAt,
        Integer saleCount,
        Integer purchaseCount,
        Long totalProductCount
) {
    public static StoreDetailResDto of(
            Long sellerId,
            String nickname,
            String profileImageUrl,
            LocalDateTime storeStartAt,
            Integer saleCount,
            Integer purchaseCount,
            Long totalProductCount
    ) {
        return new StoreDetailResDto(
                sellerId,
                nickname,
                profileImageUrl,
                storeStartAt,
                saleCount,
                purchaseCount,
                totalProductCount
        );
    }
}