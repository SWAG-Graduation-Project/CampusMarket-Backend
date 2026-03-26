package com.campusmarket.backend.domain.wishlist.constant;

import com.campusmarket.backend.global.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum WishlistErrorCode implements BaseErrorCode {

    PRODUCT_NOT_FOUND("WISHLIST_001", "해당 상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    WISHLIST_FORBIDDEN_PRODUCT("WISHLIST_002", "찜할 수 없는 상품입니다.", HttpStatus.BAD_REQUEST),
    INVALID_PAGE_REQUEST("WISHLIST_003", "페이지 요청 값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}