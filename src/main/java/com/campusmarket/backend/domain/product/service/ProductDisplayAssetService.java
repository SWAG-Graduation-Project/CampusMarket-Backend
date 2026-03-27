package com.campusmarket.backend.domain.product.service;

import com.campusmarket.backend.domain.product.entity.ProductDisplayAsset;
import com.campusmarket.backend.domain.product.repository.ProductDisplayAssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductDisplayAssetService {

    private final ProductDisplayAssetRepository productDisplayAssetRepository;

    public String resolveDisplayAssetImage(
            Long majorCategoryId,
            Long subCategoryId,
            String color,
            String productName
    ) {
        String normalizedColor = normalizeColor(color);

        if (subCategoryId != null && StringUtils.hasText(normalizedColor)) {
            return productDisplayAssetRepository
                    .findFirstByMajorCategory_IdAndSubCategory_IdAndColorAndActiveTrueOrderBySortOrderAsc(
                            majorCategoryId,
                            subCategoryId,
                            normalizedColor
                    )
                    .map(ProductDisplayAsset::getImageUrl)
                    .orElseGet(() -> fallbackBySubCategory(majorCategoryId, subCategoryId));
        }

        if (subCategoryId != null) {
            return fallbackBySubCategory(majorCategoryId, subCategoryId);
        }

        return fallbackByMajorCategory(majorCategoryId);
    }

    private String fallbackBySubCategory(Long majorCategoryId, Long subCategoryId) {
        return productDisplayAssetRepository
                .findFirstByMajorCategory_IdAndSubCategory_IdAndActiveTrueOrderBySortOrderAsc(
                        majorCategoryId,
                        subCategoryId
                )
                .map(ProductDisplayAsset::getImageUrl)
                .orElseGet(() -> fallbackByMajorCategory(majorCategoryId));
    }

    private String fallbackByMajorCategory(Long majorCategoryId) {
        return productDisplayAssetRepository
                .findFirstByMajorCategory_IdAndActiveTrueOrderBySortOrderAsc(majorCategoryId)
                .map(ProductDisplayAsset::getImageUrl)
                .orElseGet(this::fallbackDefault);
    }

    private String fallbackDefault() {
        return productDisplayAssetRepository
                .findFirstByActiveTrueOrderBySortOrderAsc()
                .map(ProductDisplayAsset::getImageUrl)
                .orElse("/assets/shop/default/default_item.png");
    }

    private String normalizeColor(String color) {
        if (!StringUtils.hasText(color)) {
            return null;
        }

        String normalized = color.trim();

        return switch (normalized) {
            case "화이트", "아이보리" -> "흰색";
            case "블랙" -> "검정";
            case "브라운" -> "갈색";
            case "네이비" -> "남색";
            case "레드" -> "빨강";
            case "블루" -> "파랑";
            default -> normalized;
        };
    }
}