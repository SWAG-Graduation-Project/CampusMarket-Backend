package com.campusmarket.backend.domain.store.controller;

import com.campusmarket.backend.domain.store.dto.response.MyStoreMainResDto;
import com.campusmarket.backend.domain.store.dto.response.MyStoreProductListResDto;
import com.campusmarket.backend.domain.store.dto.response.StoreDetailResDto;
import com.campusmarket.backend.domain.store.dto.response.StoreListResDto;
import com.campusmarket.backend.domain.store.dto.response.StoreProductListResDto;
import com.campusmarket.backend.domain.store.service.StoreService;
import com.campusmarket.backend.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class StoreController implements StoreControllerDocs {

    private final StoreService storeService;

    @Override
    @GetMapping("/stores")
    public ApiResponse<StoreListResDto> getStores(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "12") Integer size
    ) {
        return ApiResponse.success(storeService.getStores(page, size));
    }

    @Override
    @GetMapping("/stores/{sellerId}")
    public ApiResponse<StoreDetailResDto> getStoreDetail(@PathVariable Long sellerId) {
        return ApiResponse.success(storeService.getStoreDetail(sellerId));
    }

    @Override
    @GetMapping("/stores/{sellerId}/products")
    public ApiResponse<StoreProductListResDto> getStoreProducts(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return ApiResponse.success(storeService.getStoreProducts(sellerId, page, size));
    }

    @Override
    @GetMapping("/my-store")
    public ApiResponse<MyStoreMainResDto> getMyStore(
            @RequestHeader("X-Guest-UUID") String guestUuid
    ) {
        return ApiResponse.success(storeService.getMyStore(guestUuid));
    }

    @Override
    @GetMapping("/my-store/products")
    public ApiResponse<MyStoreProductListResDto> getMyStoreProducts(
            @RequestHeader("X-Guest-UUID") String guestUuid,
            @RequestParam(required = false) String saleStatus,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return ApiResponse.success(
                storeService.getMyStoreProducts(guestUuid, saleStatus, page, size)
        );
    }
}