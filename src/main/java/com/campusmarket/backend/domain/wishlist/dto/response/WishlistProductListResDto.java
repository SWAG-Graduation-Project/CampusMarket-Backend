package com.campusmarket.backend.domain.wishlist.dto.response;

import java.util.List;

public record WishlistProductListResDto(
        List<WishlistProductResDto> products,
        Integer page,
        Integer size,
        Boolean hasNext
) {
    public static WishlistProductListResDto of(
            List<WishlistProductResDto> products,
            Integer page,
            Integer size,
            Boolean hasNext
    ) {
        return new WishlistProductListResDto(products, page, size, hasNext);
    }
}