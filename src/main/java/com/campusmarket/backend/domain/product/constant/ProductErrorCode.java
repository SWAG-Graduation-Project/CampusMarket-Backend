package com.campusmarket.backend.domain.product.constant;

import com.campusmarket.backend.global.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements BaseErrorCode {

    PRODUCT_MAJOR_CATEGORY_NOT_FOUND("PRODUCT_001", "존재하지 않는 대카테고리입니다.", HttpStatus.NOT_FOUND),
    PRODUCT_SUB_CATEGORY_NOT_FOUND("PRODUCT_002", "존재하지 않는 소카테고리입니다.", HttpStatus.NOT_FOUND),
    PRODUCT_INVALID_CATEGORY_RELATION("PRODUCT_003", "대카테고리와 소카테고리의 관계가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_INVALID_PRICE("PRODUCT_004", "무료나눔 상품은 가격이 0이어야 합니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_IMAGES_REQUIRED("PRODUCT_005", "상품 이미지는 최소 1개 이상 필요합니다.", HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_DATE_TYPE("PRODUCT_006", "상품 날짜 데이터 타입이 올바르지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;
}