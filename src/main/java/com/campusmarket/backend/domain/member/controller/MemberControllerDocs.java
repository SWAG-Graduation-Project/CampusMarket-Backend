package com.campusmarket.backend.domain.member.controller;
import com.campusmarket.backend.domain.member.dto.request.MemberProfileCreateReqDto;
import com.campusmarket.backend.domain.member.dto.request.MemberProfileUpdateReqDto;
import com.campusmarket.backend.domain.member.dto.response.MemberProfileResDto;
import com.campusmarket.backend.domain.member.dto.response.NicknameCheckResDto;
import com.campusmarket.backend.domain.member.dto.response.RandomNicknameResDto;
import com.campusmarket.backend.global.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Member", description = "회원 API")
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

    @Operation(summary = "회원 프로필 최초 입력", description = "회원 온보딩 과정에서 프로필 정보를 최초 저장합니다.")
    ApiResponse<MemberProfileResDto> createProfile(
            @Parameter(description = "게스트 UUID", required = true)
            @RequestHeader("guestUuid") String guestUuid,
            @Valid @RequestBody MemberProfileCreateReqDto reqDto
    );

    @Operation(summary = "회원 프로필 수정", description = "회원 프로필 정보를 수정합니다.")
    ApiResponse<MemberProfileResDto> updateProfile(
            @Parameter(description = "게스트 UUID", required = true)
            @RequestHeader("guestUuid") String guestUuid,
            @Valid @RequestBody MemberProfileUpdateReqDto reqDto
    );

    @Operation(summary = "내 회원 프로필 조회", description = "현재 회원의 프로필 정보를 조회합니다.")
    ApiResponse<MemberProfileResDto> getProfile(
            @Parameter(description = "게스트 UUID", required = true)
            @RequestHeader("guestUuid") String guestUuid
    );
}
