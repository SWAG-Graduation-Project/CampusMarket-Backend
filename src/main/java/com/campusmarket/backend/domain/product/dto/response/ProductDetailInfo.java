package com.campusmarket.backend.domain.product.dto.response;

import com.campusmarket.backend.domain.product.entity.ProductCondition;
import com.campusmarket.backend.domain.product.entity.ProductSaleStatus;

import java.time.LocalDateTime;

public record ProductDetailInfo(
        Long productId,
        Long sellerId,
        Long majorCategoryId,
        String majorCategoryName,
        Long subCategoryId,
        String subCategoryName,
        String name,
        String brand,
        String color,
        ProductCondition productCondition,
        String description,
        Integer price,
        Boolean isFree,
        ProductSaleStatus saleStatus,
        Integer viewCount,
        Integer wishCount,
        LocalDateTime createdAt,
        String sellerNickname,
        String sellerProfileImageUrl,
        LocalDateTime storeStartDate,
        Integer saleCount,
        String displayAssetImageUrl
) {
}