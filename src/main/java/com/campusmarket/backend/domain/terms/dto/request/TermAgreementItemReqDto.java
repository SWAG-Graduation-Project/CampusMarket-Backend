package com.campusmarket.backend.domain.terms.dto.request;

import com.campusmarket.backend.domain.terms.constant.TermCode;

// 약관 동의 요청 항목
public record TermAgreementItemReqDto(
        TermCode termCode,
        Boolean agreed
) {
}
