package com.campusmarket.backend.domain.store.dto.response;

public record StoreSummaryResDto(
        Long sellerId,
        String nickname,
        String profileImageUrl,
        String latestProductImageUrl,
        Integer saleCount,
        Integer purchaseCount
) {
    public static StoreSummaryResDto of(
            Long sellerId,
            String nickname,
            String profileImageUrl,
            String latestProductImageUrl,
            Integer saleCount,
            Integer purchaseCount
    ) {
        return new StoreSummaryResDto(
                sellerId,
                nickname,
                profileImageUrl,
                latestProductImageUrl,
                saleCount,
                purchaseCount
        );
    }
}