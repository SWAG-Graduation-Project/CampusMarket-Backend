package com.campusmarket.backend.domain.terms.dto.response;

// 약관 동의 저장 응답
public record TermsAgreementSaveResDto(
        Boolean termsCompleted
) {
    public static TermsAgreementSaveResDto of(Boolean termsCompleted) {
        return new TermsAgreementSaveResDto(termsCompleted);
    }
}
