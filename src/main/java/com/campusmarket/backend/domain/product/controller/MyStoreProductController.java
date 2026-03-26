package com.campusmarket.backend.domain.product.controller;

import com.campusmarket.backend.domain.product.dto.request.ProductUpdateReqDto;
import com.campusmarket.backend.domain.product.dto.response.ProductDetailResDto;
import com.campusmarket.backend.domain.product.service.ProductService;
import com.campusmarket.backend.global.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MyStoreProductController implements MyStoreProductControllerDocs {

    private final ProductService productService;

    @Override
    @GetMapping("/{productId}")
    public ApiResponse<ProductDetailResDto> getMyProductDetail(
            @PathVariable Long productId,
            @RequestHeader("X-Guest-UUID") String guestUuid
    ) {
        return ApiResponse.success(productService.getMyProductDetail(productId, guestUuid));
    }

    @Override
    @PatchMapping("/{productId}")
    public ApiResponse<Void> updateProduct(
            @PathVariable Long productId,
            @RequestHeader("X-Guest-UUID") String guestUuid,
            @Valid @RequestBody ProductUpdateReqDto reqDto
    ) {
        productService.updateProduct(productId, guestUuid, reqDto);
        return ApiResponse.success(null);
    }

    @Override
    @DeleteMapping("/{productId}")
    public ApiResponse<Void> deleteProduct(
            @PathVariable Long productId,
            @RequestHeader("X-Guest-UUID") String guestUuid
    ) {
        productService.deleteProduct(productId, guestUuid);
        return ApiResponse.success(null);
    }

    @Override
    @PatchMapping("/{productId}/sold")
    public ApiResponse<Void> completeSale(
            @PathVariable Long productId,
            @RequestHeader("X-Guest-UUID") String guestUuid
    ) {
        productService.completeSale(productId, guestUuid);
        return ApiResponse.success(null);
    }
}