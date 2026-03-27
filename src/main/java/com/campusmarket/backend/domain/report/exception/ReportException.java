package com.campusmarket.backend.domain.report.exception;

import com.campusmarket.backend.global.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public class ReportException extends RuntimeException {

    private final BaseErrorCode errorCode;

    public ReportException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
