package com.campusmarket.backend.domain.member.dto.request;

import jakarta.validation.constraints.Size;

public record MemberProfileUpdateReqDto(
        @Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하여야 합니다.")
        String nickname,

        String profileImageUrl,
        String lockerName,
        String timetableData  // JSON: {"classes": [...]}
) {
}
