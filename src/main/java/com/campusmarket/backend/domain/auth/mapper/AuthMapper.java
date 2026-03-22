package com.campusmarket.backend.domain.auth.mapper;

import com.campusmarket.backend.domain.auth.dto.CurrentMemberInfoRespDto;
import com.campusmarket.backend.domain.auth.dto.GuestCreateReqDto;
import com.campusmarket.backend.domain.auth.dto.GuestCreateRespDto;
import com.campusmarket.backend.domain.member.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {
    public GuestCreateRespDto toGuestCreateRespDto(Member member, boolean isNewMember){
        return new GuestCreateRespDto(
                member.getId(),
                member.getGuestUuid(),
                member.getLoginType(),
                member.getStatus(),
                isNewMember
        );
    }

    public CurrentMemberInfoRespDto toCurrentMemberInfoRespDto(Member member){
        return new CurrentMemberInfoRespDto(
                member.getId(),
                member.getGuestUuid(),
                member.getLoginType(),
                member.getStatus()
        );
    }
}
