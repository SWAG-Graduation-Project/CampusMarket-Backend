package com.campusmarket.backend.domain.terms.dto.response;

import java.util.List;

// 내 약관 동의 내역
public record MyTermsAgreementResDto(
        List<MyTermAgreementItemResDto> agreements
) {
    public static MyTermsAgreementResDto of(List<MyTermAgreementItemResDto> agreements) {
        return new MyTermsAgreementResDto(agreements);
    }
}
