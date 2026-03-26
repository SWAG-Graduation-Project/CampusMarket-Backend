package com.campusmarket.backend.domain.wishlist.repository;

import com.campusmarket.backend.domain.wishlist.dto.response.WishlistProductResDto;

import java.util.List;

public interface WishlistQueryRepository {

    List<WishlistProductResDto> findWishlistProducts(
            Long memberId,
            int offset,
            int limit
    );

    long countWishlistProducts(Long memberId);
}