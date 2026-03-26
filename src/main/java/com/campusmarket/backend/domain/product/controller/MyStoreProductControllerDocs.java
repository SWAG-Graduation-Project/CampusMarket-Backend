package com.campusmarket.backend.domain.product.controller;

import com.campusmarket.backend.domain.product.dto.request.ProductUpdateReqDto;
import com.campusmarket.backend.domain.product.dto.response.ProductDetailResDto;
import com.campusmarket.backend.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "My Store Product", description = "내 상점 상품 관리 API")
@RequestMapping("/my-store/products")
public interface MyStoreProductControllerDocs {

    @Operation(summary = "내 상품 상세 조회", description = "수정 화면 진입용 내 상품 상세 정보를 조회합니다.")
    ApiResponse<ProductDetailResDto> getMyProductDetail(
            @PathVariable Long productId,
            @RequestHeader("X-Guest-UUID") String guestUuid
    );

    @Operation(summary = "상품 수정", description = "내 상품 정보를 수정합니다.")
    ApiResponse<Void> updateProduct(
            @PathVariable Long productId,
            @RequestHeader("X-Guest-UUID") String guestUuid,
            @Valid @RequestBody ProductUpdateReqDto reqDto
    );

    @Operation(summary = "상품 삭제", description = "내 상품을 삭제합니다.")
    ApiResponse<Void> deleteProduct(
            @PathVariable Long productId,
            @RequestHeader("X-Guest-UUID") String guestUuid
    );

    @Operation(summary = "판매 완료 처리", description = "내 상품의 판매 상태를 SOLD로 변경합니다.")
    ApiResponse<Void> completeSale(
            @PathVariable Long productId,
            @RequestHeader("X-Guest-UUID") String guestUuid
    );
}