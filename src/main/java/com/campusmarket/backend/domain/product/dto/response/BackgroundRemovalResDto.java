package com.campusmarket.backend.domain.product.dto.response;

import java.util.List;

public record BackgroundRemovalResDto(
        List<BackgroundRemovalItemResDto> items
) {
    public static BackgroundRemovalResDto of(List<BackgroundRemovalItemResDto> items) {
        return new BackgroundRemovalResDto(items);
    }
}