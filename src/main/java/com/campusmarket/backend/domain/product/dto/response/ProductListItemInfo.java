package com.campusmarket.backend.domain.product.dto.response;

import com.campusmarket.backend.domain.product.entity.ProductCondition;
import com.campusmarket.backend.domain.product.entity.ProductSaleStatus;

import java.time.LocalDateTime;

// 내부 조회용 info record
public record ProductListItemInfo(
        Long productId,
        String name,
        String brand,
        Integer price,
        Boolean isFree,
        ProductCondition productCondition,
        ProductSaleStatus saleStatus,
        Integer viewCount,
        Integer wishCount,
        String displayAssetImageUrl,
        String thumbnailImageUrl,
        LocalDateTime createdAt,
        Long sellerId,
        String sellerNickname
) {
}