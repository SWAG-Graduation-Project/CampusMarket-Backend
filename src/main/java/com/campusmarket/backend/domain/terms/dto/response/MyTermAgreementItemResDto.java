package com.campusmarket.backend.domain.terms.dto.response;

import com.campusmarket.backend.domain.terms.constant.TermCode;

import java.time.LocalDateTime;

// 내 약관 동의 항목
public record MyTermAgreementItemResDto(
        TermCode termCode,
        String title,
        Boolean required,
        Boolean agreed,
        LocalDateTime agreedAt
) {
    public static MyTermAgreementItemResDto of(
            TermCode termCode,
            String title,
            Boolean required,
            Boolean agreed,
            LocalDateTime agreedAt
    ) {
        return new MyTermAgreementItemResDto(
                termCode,
                title,
                required,
                agreed,
                agreedAt
        );
    }
}
