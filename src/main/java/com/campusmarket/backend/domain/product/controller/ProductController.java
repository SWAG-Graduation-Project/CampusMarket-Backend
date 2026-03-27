package com.campusmarket.backend.domain.product.controller;

import com.campusmarket.backend.domain.member.constant.MemberErrorCode;
import com.campusmarket.backend.domain.member.entity.Member;
import com.campusmarket.backend.domain.member.exception.MemberException;
import com.campusmarket.backend.domain.member.repository.MemberRepository;
import com.campusmarket.backend.domain.product.dto.request.*;
import com.campusmarket.backend.domain.product.dto.response.*;
import com.campusmarket.backend.domain.product.service.ProductAiService;
import com.campusmarket.backend.domain.product.service.ProductService;
import com.campusmarket.backend.global.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductControllerDocs {

    private final ProductService productService;
    private final ProductAiService productAiService;
    private final MemberRepository memberRepository;

    @Override
    @GetMapping
    public ApiResponse<ProductListResDto> searchProducts(@ModelAttribute SearchProductsReqDto reqDto) {
        return ApiResponse.success(productService.searchProducts(reqDto));
    }

    @Override
    @GetMapping("/{productId:\\d+}")
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

    @Override
    @PostMapping("/images/temp")
    public ApiResponse<ProductTempImageResDto> uploadTempImage(
            @RequestHeader(value = "X-Guest-UUID", required = false) String guestUuid,
            @RequestHeader(value = "X-Member-Id", required = false) Long memberId,
            @RequestParam("files") MultipartFile file
    ) {
        Member member = resolveMember(memberId, guestUuid);

        return ApiResponse.success(
                productService.uploadTempImage(member.getId(), file)
        );
    }

    @Override
    @PostMapping("/draft")
    public ApiResponse<ProductDraftResDto> generateDraft(
            @RequestHeader(value = "X-Guest-UUID", required = false) String guestUuid,
            @RequestHeader(value = "X-Member-Id", required = false) Long memberId,
            @Valid @RequestBody ProductDraftReqDto reqDto
    ) {
        Member member = resolveMember(memberId, guestUuid);

        return ApiResponse.success(
                productAiService.generateDraft(member.getId(), reqDto.tempImageIds())
        );
    }

    @Override
    @PostMapping("/images/background-removal")
    public ApiResponse<BackgroundRemovalResDto> removeBackground(
            @RequestHeader(value = "X-Guest-UUID", required = false) String guestUuid,
            @RequestHeader(value = "X-Member-Id", required = false) Long memberId,
            @Valid @RequestBody BackgroundRemovalReqDto reqDto
    ) {
        Member member = resolveMember(memberId, guestUuid);

        return ApiResponse.success(
                productAiService.removeBackground(member.getId(), reqDto.tempImageIds())
        );
    }

    @Override
    @PostMapping("/images/temp/{tempImageId:\\d+}/replace")
    public ApiResponse<ProductTempImageResDto> replaceTempImage(
            @PathVariable Long tempImageId,
            @RequestHeader(value = "X-Guest-UUID", required = false) String guestUuid,
            @RequestHeader(value = "X-Member-Id", required = false) Long memberId,
            @RequestParam("files") MultipartFile file
    ) {
        Member member = resolveMember(memberId, guestUuid);

        return ApiResponse.success(
                productService.replaceTempImage(member.getId(), tempImageId, file)
        );
    }

    @Override
    @DeleteMapping("/images/temp/{tempImageId:\\d+}")
    public ApiResponse<Void> deleteTempImage(
            @PathVariable Long tempImageId,
            @RequestHeader(value = "X-Guest-UUID", required = false) String guestUuid,
            @RequestHeader(value = "X-Member-Id", required = false) Long memberId
    ) {
        Member member = resolveMember(memberId, guestUuid);

        productService.deleteTempImage(member.getId(), tempImageId);
        return ApiResponse.success();
    }

    @Override
    @PostMapping("/draft/redraft")
    public ApiResponse<ProductDraftResDto> redraft(
            @RequestHeader(value = "X-Guest-UUID", required = false) String guestUuid,
            @RequestHeader(value = "X-Member-Id", required = false) Long memberId,
            @Valid @RequestBody ProductRedraftReqDto reqDto
    ) {
        Member member = resolveMember(memberId, guestUuid);

        return ApiResponse.success(
                productAiService.redraft(member.getId(), reqDto)
        );
    }


    private Member resolveMember(Long memberId, String guestUuid) {
        if (memberId != null) {
            return memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        }

        if (StringUtils.hasText(guestUuid)) {
            return memberRepository.findByGuestUuid(guestUuid)
                    .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        }

        throw new MemberException(MemberErrorCode.INVALID_GUEST_UUID);
    }
}