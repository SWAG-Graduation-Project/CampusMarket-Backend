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
    INVALID_PRODUCT_SORT("PRODUCT_005", "정렬 조건이 올바르지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}