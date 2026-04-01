package com.campusmarket.backend.domain.store.dto.response;

import java.util.List;

public record StoreProductListResDto(
        List<StoreProductSummaryResDto> products,
        Integer page,
        Integer size,
        Boolean hasNext
) {
    public static StoreProductListResDto of(
            List<StoreProductSummaryResDto> products,
            Integer page,
            Integer size,
            Boolean hasNext
    ) {
        return new StoreProductListResDto(products, page, size, hasNext);
    }
}