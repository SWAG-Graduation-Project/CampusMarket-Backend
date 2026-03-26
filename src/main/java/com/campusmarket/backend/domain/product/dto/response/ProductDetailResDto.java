package com.campusmarket.backend.domain.product.dto.response;

import com.campusmarket.backend.domain.product.entity.Product;
import com.campusmarket.backend.domain.product.entity.ProductCondition;
import com.campusmarket.backend.domain.product.entity.ProductSaleStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ProductDetailResDto(
        Long productId,
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
        ProductCategoryResDto category,
        ProductSellerResDto seller,
        List<ProductImageResDto> images,
        Boolean isWished,
        Boolean canChat
) {
    public static ProductDetailResDto of(
            Long productId,
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
            ProductCategoryResDto category,
            ProductSellerResDto seller,
            List<ProductImageResDto> images,
            Boolean isWished,
            Boolean canChat
    ) {
        return new ProductDetailResDto(
                productId,
                name,
                brand,
                color,
                productCondition,
                description,
                price,
                isFree,
                saleStatus,
                viewCount,
                wishCount,
                createdAt,
                category,
                seller,
                images,
                isWished,
                canChat
        );
    }

    public static ProductDetailResDto from(Product product) {
        return ProductDetailResDto.of(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getColor(),
                product.getProductCondition(),
                product.getDescription(),
                product.getPrice(),
                product.getIsFree(),
                product.getSaleStatus(),
                product.getViewCount(),
                product.getWishCount(),
                product.getCreatedAt(),
                null,
                null,
                List.of(),
                false,
                false
        );
    }
}