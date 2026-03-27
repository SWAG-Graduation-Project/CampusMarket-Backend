package com.campusmarket.backend.domain.chat.exception;

import com.campusmarket.backend.global.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public class ChatException extends RuntimeException {

    private final BaseErrorCode errorCode;

    public ChatException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
