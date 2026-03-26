package com.campusmarket.backend.domain.store.repository;

import com.campusmarket.backend.domain.product.entity.ProductSaleStatus;
import com.campusmarket.backend.domain.store.dto.response.MyStoreLatestProductResDto;
import com.campusmarket.backend.domain.store.dto.response.MyStoreProductSummaryResDto;
import com.campusmarket.backend.domain.store.dto.response.StoreSummaryResDto;

import java.util.List;

public interface StoreQueryRepository {

    List<StoreSummaryResDto> findStoreSummaries(int offset, int limit);

    long countStores();

    long countProductsBySellerId(Long sellerId);

    List<MyStoreLatestProductResDto> findLatestProductsBySellerId(Long sellerId, int limit);

    List<MyStoreProductSummaryResDto> findMyStoreProducts(
            Long sellerId,
            ProductSaleStatus saleStatus,
            int offset,
            int limit
    );

    long countMyStoreProducts(Long sellerId, ProductSaleStatus saleStatus);
}