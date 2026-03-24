package com.campusmarket.backend.domain.terms.exception;

import com.campusmarket.backend.global.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public class TermsException extends RuntimeException{
    private final BaseErrorCode errorCode;

    public TermsException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
