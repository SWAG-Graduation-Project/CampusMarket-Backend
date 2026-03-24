package com.campusmarket.backend.domain.category.exception;

import com.campusmarket.backend.global.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public class CategoryException extends RuntimeException {

    private final BaseErrorCode errorCode;

    public CategoryException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
