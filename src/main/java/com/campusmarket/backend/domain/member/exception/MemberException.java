package com.campusmarket.backend.domain.member.exception;

import com.campusmarket.backend.domain.member.constant.MemberErrorCode;
import lombok.Getter;

@Getter
public class MemberException extends RuntimeException{
    private final MemberErrorCode errorCode;

    public MemberException(MemberErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
