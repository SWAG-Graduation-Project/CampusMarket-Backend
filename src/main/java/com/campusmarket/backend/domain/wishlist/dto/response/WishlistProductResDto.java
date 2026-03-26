package com.campusmarket.backend.domain.wishlist.dto.response;

import java.time.LocalDateTime;

public record WishlistProductResDto(
        Long productId,
        String productName,
        Integer price,
        String saleStatus,
        String thumbnailImageUrl,
        Integer wishCount,
        LocalDateTime createdAt
) {
    public static WishlistProductResDto of(
            Long productId,
            String productName,
            Integer price,
            String saleStatus,
            String thumbnailImageUrl,
            Integer wishCount,
            LocalDateTime createdAt
    ) {
        return new WishlistProductResDto(
                productId,
                productName,
                price,
                saleStatus,
                thumbnailImageUrl,
                wishCount,
                createdAt
        );
    }
}