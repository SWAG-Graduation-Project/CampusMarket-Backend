package com.campusmarket.backend.domain.product.exception;

import com.campusmarket.backend.global.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements BaseErrorCode {

    PRODUCT_NOT_FOUND("PRODUCT_001", "해당 상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_PRODUCT_ID("PRODUCT_002", "상품 ID가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_PAGE("PRODUCT_003", "페이지 번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_SIZE("PRODUCT_004", "페이지 크기가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_SORT("PRODUCT_005", "정렬 조건이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_INVALID_DATE_TYPE("PRODUCT_006", "상품 날짜 데이터 타입이 올바르지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    PRODUCT_MAJOR_CATEGORY_NOT_FOUND("PRODUCT_007", "존재하지 않는 대카테고리입니다.", HttpStatus.NOT_FOUND),
    PRODUCT_SUB_CATEGORY_NOT_FOUND("PRODUCT_002", "존재하지 않는 소카테고리입니다.", HttpStatus.NOT_FOUND),
    PRODUCT_INVALID_CATEGORY_RELATION("PRODUCT_009", "대카테고리와 소카테고리의 관계가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_INVALID_PRICE("PRODUCT_010", "무료나눔 상품은 가격이 0이어야 합니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_IMAGES_REQUIRED("PRODUCT_011", "상품 이미지는 최소 1개 이상 필요합니다.", HttpStatus.BAD_REQUEST);


    private final String code;
    private final String message;
    private final HttpStatus status;
}