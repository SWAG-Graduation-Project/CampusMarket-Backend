package com.campusmarket.backend.domain.terms.dto.response;

import java.util.List;

// 약관 목록 응답
public record TermsListResDto(
        List<TermItemResDto> terms
) {
    public static TermsListResDto of(List<TermItemResDto> terms){
        return new TermsListResDto(terms);
    }
}
