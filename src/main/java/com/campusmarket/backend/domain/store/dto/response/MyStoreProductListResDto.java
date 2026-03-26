package com.campusmarket.backend.domain.store.dto.response;

import java.util.List;

public record MyStoreProductListResDto(
        List<MyStoreProductSummaryResDto> products,
        Integer page,
        Integer size,
        Boolean hasNext
) {
    public static MyStoreProductListResDto of(
            List<MyStoreProductSummaryResDto> products,
            Integer page,
            Integer size,
            Boolean hasNext
    ) {
        return new MyStoreProductListResDto(products, page, size, hasNext);
    }
}