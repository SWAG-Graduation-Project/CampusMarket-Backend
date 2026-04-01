package com.campusmarket.backend.domain.store.dto.response;

import java.time.LocalDateTime;

public record StoreSummaryResDto(
        Long sellerId,
        String sellerNickname,
        Long latestProductId,
        String latestProductDisplayAssetImageUrl,
        LocalDateTime latestProductCreatedAt
) {
    public static StoreSummaryResDto of(
            Long sellerId,
            String sellerNickname,
            Long latestProductId,
            String latestProductDisplayAssetImageUrl,
            LocalDateTime latestProductCreatedAt
    ) {
        return new StoreSummaryResDto(
                sellerId,
                sellerNickname,
                latestProductId,
                latestProductDisplayAssetImageUrl,
                latestProductCreatedAt
        );
    }
}