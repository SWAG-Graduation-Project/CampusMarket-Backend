package com.campusmarket.backend.domain.store.dto.response;

public record MyStoreLatestProductResDto(
        Long productId,
        String productName,
        Integer price,
        String thumbnailImageUrl
) {
    public static MyStoreLatestProductResDto of(
            Long productId,
            String productName,
            Integer price,
            String thumbnailImageUrl
    ) {
        return new MyStoreLatestProductResDto(
                productId,
                productName,
                price,
                thumbnailImageUrl
        );
    }
}