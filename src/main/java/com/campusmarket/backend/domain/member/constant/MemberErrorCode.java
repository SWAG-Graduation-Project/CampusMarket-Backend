package com.campusmarket.backend.domain.member.constant;

import com.campusmarket.backend.global.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {
    INVALID_GUEST_UUID("MEMBER_001", "guestUuid가 비어 있습니다.", HttpStatus.BAD_REQUEST),
    INVALID_GUEST_UUID_FORMAT("MEMBER_002", "guestUuid 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    MEMBER_NOT_FOUND("MEMBER_003", "해당 회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    INVALID_NICKNAME("MEMBER_004", "닉네임 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_NICKNAME("MEMBER_005", "이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),
    RANDOM_NICKNAME_WORD_NOT_FOUND("MEMBER_006", "랜덤 닉네임 생성에 필요한 단어를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    RANDOM_NICKNAME_GENERATION_FAILED("MEMBER_007", "랜덤 닉네임 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);


    private final String code;
    private final String message;
    private final HttpStatus status;
}
