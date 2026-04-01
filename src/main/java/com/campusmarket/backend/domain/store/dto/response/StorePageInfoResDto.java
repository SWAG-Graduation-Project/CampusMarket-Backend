package com.campusmarket.backend.domain.store.dto.response;

public record StorePageInfoResDto(
        Integer page,
        Integer size,
        Long totalElements,
        Integer totalPages,
        Boolean hasNext
) {
    public static StorePageInfoResDto of(
            Integer page,
            Integer size,
            Long totalElements,
            Integer totalPages,
            Boolean hasNext
    ) {
        return new StorePageInfoResDto(
                page,
                size,
                totalElements,
                totalPages,
                hasNext
        );
    }
}