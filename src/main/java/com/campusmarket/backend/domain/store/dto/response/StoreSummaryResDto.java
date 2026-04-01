package com.campusmarket.backend.domain.store.dto.response;

public record StoreSummaryResDto(
        Long sellerId,
        String nickname,
        String profileImageUrl,
        Long latestProductId,
        String latestProductImageUrl,
        Integer saleCount,
        Integer purchaseCount
) {
    public static StoreSummaryResDto of(
            Long sellerId,
            String nickname,
            String profileImageUrl,
            Long latestProductId,
            String latestProductImageUrl,
            Integer saleCount,
            Integer purchaseCount
    ) {
        return new StoreSummaryResDto(
                sellerId,
                nickname,
                profileImageUrl,
                latestProductId,
                latestProductImageUrl,
                saleCount,
                purchaseCount
        );
    }
}