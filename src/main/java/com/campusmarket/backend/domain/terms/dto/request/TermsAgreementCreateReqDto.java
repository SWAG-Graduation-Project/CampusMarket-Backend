package com.campusmarket.backend.domain.terms.dto.request;

import java.util.List;

// 약관 동의 저장 요청
public record TermsAgreementCreateReqDto(
        List<TermAgreementItemReqDto> agreements
) {
}
