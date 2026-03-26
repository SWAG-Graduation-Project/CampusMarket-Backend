package com.campusmarket.backend.domain.product.controller;

import com.campusmarket.backend.domain.product.dto.request.ProductCreateReqDto;
import com.campusmarket.backend.domain.product.dto.request.SearchProductsReqDto;
import com.campusmarket.backend.domain.product.dto.response.ProductCreateResDto;
import com.campusmarket.backend.domain.product.dto.response.ProductDetailResDto;
import com.campusmarket.backend.domain.product.dto.response.ProductListResDto;
import com.campusmarket.backend.domain.product.dto.response.ProductViewIncreaseResDto;
import com.campusmarket.backend.domain.product.service.ProductService;
import com.campusmarket.backend.global.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductControllerDocs {

    private final ProductService productService;

    @Override
    @GetMapping
    public ApiResponse<ProductListResDto> searchProducts(@ModelAttribute SearchProductsReqDto reqDto) {
        return ApiResponse.success(productService.searchProducts(reqDto));
    }

    @Override
    @GetMapping("/{productId}")
    public ApiResponse<ProductDetailResDto> getProductDetail(
            @PathVariable Long productId,
            @RequestHeader(value = "X-Member-Id", required = false) Long memberId
    ) {
        return ApiResponse.success(productService.getProductDetail(productId, memberId));
    }

    @Override
    @PostMapping("/{productId}/views")
    public ApiResponse<ProductViewIncreaseResDto> increaseViewCount(@PathVariable Long productId) {
        return ApiResponse.success(productService.increaseViewCount(productId));
    }

    @Override
    @PostMapping("/my-store/products")
    public ApiResponse<ProductCreateResDto> createProduct(
            @RequestHeader("X-Guest-UUID") String guestUuid,
            @Valid @RequestBody ProductCreateReqDto reqDto
    ) {
        return ApiResponse.success(productService.createProduct(guestUuid, reqDto));
    }
}