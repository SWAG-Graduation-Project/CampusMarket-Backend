package com.campusmarket.backend.domain.auth.dto;

import com.campusmarket.backend.domain.member.constant.LoginType;
import com.campusmarket.backend.domain.member.constant.MemberStatus;

public record GuestCreateRespDto(
        Long memberId,
        String guestUuid,
        LoginType loginType,
        MemberStatus memberStatus,
        boolean isNewMember
) {
}
