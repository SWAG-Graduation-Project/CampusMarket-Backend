package com.campusmarket.backend.domain.product.dto.response;

import com.campusmarket.backend.domain.product.entity.ProductCondition;
import com.campusmarket.backend.domain.product.entity.ProductSaleStatus;

import java.time.LocalDateTime;

public record ProductListItemResDto(
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
        LocalDateTime createdAt
) {
    public static ProductListItemResDto of(
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
            LocalDateTime createdAt
    ) {
        return new ProductListItemResDto(
                productId,
                name,
                brand,
                price,
                isFree,
                productCondition,
                saleStatus,
                viewCount,
                wishCount,
                displayAssetImageUrl,
                thumbnailImageUrl,
                createdAt
        );
    }
}