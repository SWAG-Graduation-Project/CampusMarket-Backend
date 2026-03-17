package com.campusmarket.backend.dto;

import com.campusmarket.backend.model.Member;
import com.campusmarket.backend.model.MemberRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberResponse {

    private Long id;
    private String nickname;
    private MemberRole role;
    private LocalDateTime createdAt;

    public static MemberResponse from(Member member){
        return MemberResponse.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .role(member.getRole())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
