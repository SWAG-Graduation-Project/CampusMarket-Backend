package com.campusmarket.backend.domain.store.constant;

import com.campusmarket.backend.global.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StoreErrorCode implements BaseErrorCode {

    STORE_OWNER_NOT_FOUND("STORE_001", "해당 상점 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    STORE_PROFILE_NOT_FOUND("STORE_002", "해당 상점 프로필을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_PAGE_REQUEST("STORE_003", "페이지 요청 값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_SALE_STATUS("STORE_004", "유효하지 않은 판매 상태입니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}