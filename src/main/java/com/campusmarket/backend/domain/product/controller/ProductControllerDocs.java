package com.campusmarket.backend.domain.product.controller;

import com.campusmarket.backend.domain.product.dto.request.*;
import com.campusmarket.backend.domain.product.dto.response.*;
import com.campusmarket.backend.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Product", description = "상품 조회 관련 API")
@RequestMapping("/products")
public interface ProductControllerDocs {

    @Operation(
            summary = "상품 목록 조회",
            description = "검색, 카테고리 필터, 정렬, 페이지네이션을 포함한 상품 목록 조회 API입니다."
    )
    ApiResponse<ProductListResDto> searchProducts(
            @ModelAttribute SearchProductsReqDto reqDto
    );

    @Operation(
            summary = "상품 상세 조회",
            description = "상품 정보, 이미지 목록, 판매자 정보, 찜 여부, 채팅 가능 여부를 조회합니다."
    )
    ApiResponse<ProductDetailResDto> getProductDetail(
            @Parameter(description = "상품 ID", example = "1")
            @PathVariable Long productId,
            @Parameter(description = "현재 회원 ID (임시 헤더)", example = "1")
            @RequestHeader(value = "X-Member-Id", required = false) Long memberId
    );

    @Operation(
            summary = "조회수 증가",
            description = "상품 상세 진입 시 조회수를 1 증가시킵니다."
    )
    ApiResponse<ProductViewIncreaseResDto> increaseViewCount(
            @Parameter(description = "상품 ID", example = "1")
            @PathVariable Long productId
    );

    @Operation(summary = "상품 등록", description = "판매자가 상품을 등록합니다.")
    ApiResponse<ProductCreateResDto> createProduct(
            @RequestHeader("X-Guest-UUID") String guestUuid,
            @Valid @RequestBody ProductCreateReqDto reqDto
    );

    ApiResponse<ProductTempImageResDto> uploadTempImage(
            String guestUuid,
            Long memberId,
            MultipartFile file
    );

    ApiResponse<ProductDraftResDto> generateDraft(
            String guestUuid,
            Long memberId,
            @Valid @RequestBody ProductDraftReqDto reqDto
    );

    ApiResponse<BackgroundRemovalResDto> removeBackground(
            String guestUuid,
            Long memberId,
            @Valid @RequestBody BackgroundRemovalReqDto reqDto
    );

    ApiResponse<ProductTempImageResDto> replaceTempImage(
            Long tempImageId,
            String guestUuid,
            Long memberId,
            MultipartFile file
    );

    ApiResponse<Void> deleteTempImage(
            Long tempImageId,
            String guestUuid,
            Long memberId
    );

    ApiResponse<ProductDraftResDto> redraft(
            String guestUuid,
            Long memberId,
            @Valid @RequestBody ProductRedraftReqDto reqDto
    );
}