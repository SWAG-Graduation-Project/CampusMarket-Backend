package com.campusmarket.backend.domain.store.dto.response;

import java.time.LocalDateTime;

public record StoreListItemResDto(
        Long sellerId,
        String sellerNickname,
        String latestProductDisplayAssetImageUrl,
        LocalDateTime latestProductCreatedAt
) {
    public static StoreListItemResDto of(
            Long sellerId,
            String sellerNickname,
            String latestProductDisplayAssetImageUrl,
            LocalDateTime latestProductCreatedAt
    ) {
        return new StoreListItemResDto(
                sellerId,
                sellerNickname,
                latestProductDisplayAssetImageUrl,
                latestProductCreatedAt
        );
    }
}