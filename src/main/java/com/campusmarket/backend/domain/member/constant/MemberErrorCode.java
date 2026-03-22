package com.campusmarket.backend.domain.member.constant;

import com.campusmarket.backend.global.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {
    INVALID_GUEST_UUID("MEMBER_001", "guestUuid가 비어 있습니다.", HttpStatus.BAD_REQUEST),
    MEMBER_NOT_FOUND("MEMBER_002", "해당 회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
