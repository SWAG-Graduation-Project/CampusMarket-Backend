package com.campusmarket.backend.domain.store.dto.response;

import java.time.LocalDateTime;

public record MyStoreProductSummaryResDto(
        Long productId,
        String productName,
        Integer price,
        Boolean freeShare,
        String saleStatus,
        String thumbnailImageUrl,
        Integer viewCount,
        Integer wishCount,
        LocalDateTime createdAt
) {
    public static MyStoreProductSummaryResDto of(
            Long productId,
            String productName,
            Integer price,
            Boolean freeShare,
            String saleStatus,
            String thumbnailImageUrl,
            Integer viewCount,
            Integer wishCount,
            LocalDateTime createdAt
    ) {
        return new MyStoreProductSummaryResDto(
                productId,
                productName,
                price,
                freeShare,
                saleStatus,
                thumbnailImageUrl,
                viewCount,
                wishCount,
                createdAt
        );
    }
}