package com.campusmarket.backend.domain.terms.dto.response;

import com.campusmarket.backend.domain.terms.constant.TermCode;

// 약관 목록 항목
public record TermItemResDto(
        TermCode termCode,
        String title,
        String content,
        Boolean required
) {
    public static TermItemResDto of(
            TermCode termCode,
            String title,
            String content,
            Boolean required
    ){
        return new TermItemResDto(termCode, title, content, required);
    }
}
