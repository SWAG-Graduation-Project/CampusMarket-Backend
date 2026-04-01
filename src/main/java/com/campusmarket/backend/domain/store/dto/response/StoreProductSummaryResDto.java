package com.campusmarket.backend.domain.store.dto.response;

import java.time.LocalDateTime;

public record StoreProductSummaryResDto(
        Long productId,
        Long sellerId,
        String name,
        Integer price,
        Boolean isFree,
        String saleStatus,
        Integer wishCount,
        String thumbnailImageUrl,
        String displayAssetImageUrl,
        LocalDateTime createdAt
) {
    public static StoreProductSummaryResDto of(
            Long productId,
            Long sellerId,
            String name,
            Integer price,
            Boolean isFree,
            String saleStatus,
            Integer wishCount,
            String thumbnailImageUrl,
            String displayAssetImageUrl,
            LocalDateTime createdAt
    ) {
        return new StoreProductSummaryResDto(
                productId,
                sellerId,
                name,
                price,
                isFree,
                saleStatus,
                wishCount,
                thumbnailImageUrl,
                displayAssetImageUrl,
                createdAt
        );
    }
}