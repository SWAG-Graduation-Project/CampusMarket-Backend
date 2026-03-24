package com.campusmarket.backend.domain.terms.constant;

import com.campusmarket.backend.global.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TermsErrorCode implements BaseErrorCode {

    TERM_AGREEMENTS_EMPTY("TERMS_001", "약관 동의 요청이 비어 있습니다.", HttpStatus.BAD_REQUEST),
    INVALID_TERM_CODE("TERMS_002", "유효하지 않은 약관 코드입니다.", HttpStatus.BAD_REQUEST),
    REQUIRED_TERM_NOT_AGREED("TERMS_003", "필수 약관에 동의해야 합니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
