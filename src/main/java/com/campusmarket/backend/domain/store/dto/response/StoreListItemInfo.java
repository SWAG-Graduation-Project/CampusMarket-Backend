package com.campusmarket.backend.domain.store.dto.response;

import java.time.LocalDateTime;

public record StoreListItemInfo(
        Long sellerId,
        String sellerNickname,
        String latestProductDisplayAssetImageUrl,
        LocalDateTime latestProductCreatedAt
) {
}