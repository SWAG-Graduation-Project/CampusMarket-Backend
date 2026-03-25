package com.campusmarket.backend.domain.product.dto.response;

import java.util.List;

public record ProductListResDto(
        List<ProductListItemResDto> products,
        ProductPageInfoResDto pageInfo
) {
    public static ProductListResDto of(
            List<ProductListItemResDto> products,
            ProductPageInfoResDto pageInfo
    ) {
        return new ProductListResDto(products, pageInfo);
    }
}