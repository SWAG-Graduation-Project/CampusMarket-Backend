package com.campusmarket.backend.domain.member.dto.response;

public record NicknameCheckResDto(
        String nickname,
        boolean available
) {
    public static NicknameCheckResDto of(String nickname, boolean available){
        return new NicknameCheckResDto(nickname,available);
    }
}
