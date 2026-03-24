package com.campusmarket.backend.domain.category.exception;

import com.campusmarket.backend.global.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CategoryErrorCode implements BaseErrorCode {

    MAJOR_CATEGORY_NOT_FOUND("CATEGORY_001", "해당 대카테고리를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;
}