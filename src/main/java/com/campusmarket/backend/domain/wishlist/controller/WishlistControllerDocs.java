package com.campusmarket.backend.domain.wishlist.controller;

import com.campusmarket.backend.domain.wishlist.dto.response.WishlistProductListResDto;
import com.campusmarket.backend.domain.wishlist.dto.response.WishlistToggleResDto;
import com.campusmarket.backend.global.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

public interface WishlistControllerDocs {

    ApiResponse<WishlistToggleResDto> toggleWishlist(
            @RequestHeader("X-Guest-UUID") String guestUuid,
            @PathVariable Long productId
    );

    ApiResponse<WishlistProductListResDto> getWishlistProducts(
            @RequestHeader("X-Guest-UUID") String guestUuid,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    );
}