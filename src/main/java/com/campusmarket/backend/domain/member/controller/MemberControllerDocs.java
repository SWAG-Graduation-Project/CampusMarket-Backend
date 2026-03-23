package com.campusmarket.backend.domain.member.controller;
import com.campusmarket.backend.domain.member.dto.response.NicknameCheckResDto;
import com.campusmarket.backend.domain.member.dto.response.RandomNicknameResDto;
import com.campusmarket.backend.global.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Member", description = "회원 관련 API")
public interface MemberControllerDocs {

    @Operation(
            summary = "랜덤 닉네임 추천",
            description = "가입 시 사용할 랜덤 닉네임을 추천합니다."
    )
    ApiResponse<RandomNicknameResDto> getRandomNickname();

    @Operation(
            summary = "닉네임 중복 확인",
            description = "입력한 닉네임의 사용 가능 여부를 확인합니다."
    )
    ApiResponse<NicknameCheckResDto> checkNickname(
            @Parameter(description = "중복 확인할 닉네임", required = true)
            @RequestParam String nickname
    );
}
