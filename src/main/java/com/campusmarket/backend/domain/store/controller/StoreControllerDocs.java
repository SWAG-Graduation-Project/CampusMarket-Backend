package com.campusmarket.backend.domain.store.controller;

import com.campusmarket.backend.domain.store.dto.response.MyStoreMainResDto;
import com.campusmarket.backend.domain.store.dto.response.MyStoreProductListResDto;
import com.campusmarket.backend.domain.store.dto.response.StoreDetailResDto;
import com.campusmarket.backend.domain.store.dto.response.StoreListResDto;
import com.campusmarket.backend.global.ApiResponse;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

public interface StoreControllerDocs {

    ApiResponse<StoreListResDto> getStores(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "12") Integer size
    );

    ApiResponse<StoreDetailResDto> getStoreDetail(Long sellerId);

    ApiResponse<MyStoreMainResDto> getMyStore(
            @RequestHeader("X-Guest-UUID") String guestUuid
    );

    ApiResponse<MyStoreProductListResDto> getMyStoreProducts(
            @RequestHeader("X-Guest-UUID") String guestUuid,
            @RequestParam(required = false) String saleStatus,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    );
}