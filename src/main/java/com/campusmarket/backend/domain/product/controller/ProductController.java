package com.campusmarket.backend.domain.product.controller;

import com.campusmarket.backend.domain.product.dto.request.SearchProductsReqDto;
import com.campusmarket.backend.domain.product.dto.response.ProductDetailResDto;
import com.campusmarket.backend.domain.product.dto.response.ProductListResDto;
import com.campusmarket.backend.domain.product.dto.response.ProductViewIncreaseResDto;
import com.campusmarket.backend.domain.product.service.ProductService;
import com.campusmarket.backend.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

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
}