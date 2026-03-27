package com.campusmarket.backend.domain.product.repository;

import com.campusmarket.backend.domain.product.entity.ProductDisplayAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductDisplayAssetRepository extends JpaRepository<ProductDisplayAsset, Long> {

    Optional<ProductDisplayAsset> findFirstByMajorCategory_IdAndSubCategory_IdAndColorAndActiveTrueOrderBySortOrderAsc(
            Long majorCategoryId,
            Long subCategoryId,
            String color
    );

    Optional<ProductDisplayAsset> findFirstByMajorCategory_IdAndSubCategory_IdAndActiveTrueOrderBySortOrderAsc(
            Long majorCategoryId,
            Long subCategoryId
    );

    Optional<ProductDisplayAsset> findFirstByMajorCategory_IdAndActiveTrueOrderBySortOrderAsc(
            Long majorCategoryId
    );

    Optional<ProductDisplayAsset> findFirstByActiveTrueOrderBySortOrderAsc();
}