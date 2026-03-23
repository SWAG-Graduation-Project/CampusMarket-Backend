package com.campusmarket.backend.domain.member.dto.response;

public record RandomNicknameResDto (String nickname){
    public static RandomNicknameResDto from(String nickname){
        return new RandomNicknameResDto(nickname);
    }
}