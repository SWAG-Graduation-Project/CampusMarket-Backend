package com.campusmarket.backend.domain.wishlist.controller;

import com.campusmarket.backend.domain.wishlist.dto.response.WishlistProductListResDto;
import com.campusmarket.backend.domain.wishlist.dto.response.WishlistToggleResDto;
import com.campusmarket.backend.domain.wishlist.service.WishlistService;
import com.campusmarket.backend.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class WishlistController implements WishlistControllerDocs {

    private final WishlistService wishlistService;

    @Override
    @PostMapping("/products/{productId}/wishlist")
    public ApiResponse<WishlistToggleResDto> toggleWishlist(
            @RequestHeader("X-Guest-UUID") String guestUuid,
            @PathVariable Long productId
    ) {
        return ApiResponse.success(wishlistService.toggleWishlist(guestUuid, productId));
    }

    @Override
    @GetMapping("/wishlist/products")
    public ApiResponse<WishlistProductListResDto> getWishlistProducts(
            @RequestHeader("X-Guest-UUID") String guestUuid,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return ApiResponse.success(
                wishlistService.getWishlistProducts(guestUuid, page, size)
        );
    }
}