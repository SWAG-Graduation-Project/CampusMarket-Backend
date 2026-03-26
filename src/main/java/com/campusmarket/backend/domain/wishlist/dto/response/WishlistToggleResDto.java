package com.campusmarket.backend.domain.wishlist.dto.response;

public record WishlistToggleResDto(
        Long productId,
        Boolean wished,
        Integer wishCount
) {
    public static WishlistToggleResDto of(
            Long productId,
            Boolean wished,
            Integer wishCount
    ) {
        return new WishlistToggleResDto(productId, wished, wishCount);
    }
}