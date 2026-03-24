package com.campusmarket.backend.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberProfileCreateReqDto(

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하여야 합니다.")
        String nickname,

        @NotBlank(message = "프로필 이미지 URL은 필수입니다.")
        String profileImageUrl,

        @NotBlank(message = "사물함 정보는 필수입니다.")
        String lockerName,

        @NotBlank(message = "시간표 이미지 URL은 필수입니다.")
        String timetableImageUrl
) {
}
