package com.campusmarket.backend.domain.product.exception;

import com.campusmarket.backend.global.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public class ProductException extends RuntimeException {

    private final BaseErrorCode errorCode;

    public ProductException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}