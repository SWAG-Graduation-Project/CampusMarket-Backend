package com.campusmarket.backend.domain.store.dto.response;

import java.util.List;

public record StoreListResDto(
        List<StoreSummaryResDto> stores,
        Integer page,
        Integer size,
        Boolean hasNext
) {
    public static StoreListResDto of(
            List<StoreSummaryResDto> stores,
            Integer page,
            Integer size,
            Boolean hasNext
    ) {
        return new StoreListResDto(stores, page, size, hasNext);
    }
}