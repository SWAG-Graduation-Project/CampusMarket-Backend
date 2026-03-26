package com.campusmarket.backend.domain.store.dto.response;

import java.util.List;

public record MyStoreMainResDto(
        Long memberId,
        String nickname,
        String profileImageUrl,
        Integer saleCount,
        Integer purchaseCount,
        List<MyStoreLatestProductResDto> latestProducts
) {
    public static MyStoreMainResDto of(
            Long memberId,
            String nickname,
            String profileImageUrl,
            Integer saleCount,
            Integer purchaseCount,
            List<MyStoreLatestProductResDto> latestProducts
    ) {
        return new MyStoreMainResDto(
                memberId,
                nickname,
                profileImageUrl,
                saleCount,
                purchaseCount,
                latestProducts
        );
    }
}