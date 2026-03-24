package com.campusmarket.backend.domain.member.controller;

import com.campusmarket.backend.domain.member.dto.request.MemberProfileCreateReqDto;
import com.campusmarket.backend.domain.member.dto.request.MemberProfileUpdateReqDto;
import com.campusmarket.backend.domain.member.dto.response.MemberProfileResDto;
import com.campusmarket.backend.domain.member.dto.response.NicknameCheckResDto;
import com.campusmarket.backend.domain.member.dto.response.OnboardingStatusResDto;
import com.campusmarket.backend.domain.member.dto.response.RandomNicknameResDto;
import com.campusmarket.backend.domain.member.service.MemberService;
import com.campusmarket.backend.global.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor

@RequestMapping("/members")
public class MemberController implements MemberControllerDocs{

    private final MemberService memberService;

    @Override
    @GetMapping("/random-nickname")
    public ApiResponse<RandomNicknameResDto> getRandomNickname() {
        return ApiResponse.success(memberService.getRandomNickname());
    }

    @Override
    @GetMapping("/nickname/check")
    public ApiResponse<NicknameCheckResDto> checkNickname(@RequestParam String nickname) {
        return ApiResponse.success(memberService.checkNickname(nickname));
    }

    @Override
    @PostMapping("/profile")
    public ApiResponse<MemberProfileResDto> createProfile(
            @RequestHeader("guestUuid") String guestUuid,
            @Valid @RequestBody MemberProfileCreateReqDto reqDto
    ) {
        return ApiResponse.success(memberService.createProfile(guestUuid, reqDto));
    }

    @Override
    @PatchMapping("/profile")
    public ApiResponse<MemberProfileResDto> updateProfile(
            @RequestHeader("guestUuid") String guestUuid,
            @Valid @RequestBody MemberProfileUpdateReqDto reqDto
    ) {
        return ApiResponse.success(memberService.updateProfile(guestUuid, reqDto));
    }

    @Override
    @GetMapping("/profile")
    public ApiResponse<MemberProfileResDto> getProfile(
            @RequestHeader("guestUuid") String guestUuid
    ) {
        return ApiResponse.success(memberService.getProfile(guestUuid));
    }

    @Override
    @GetMapping("/onboarding-status")
    public ApiResponse<OnboardingStatusResDto> getOnboardingStatus(
            @RequestHeader("guestUuid") String guestUuid
    ){
        return ApiResponse.success(memberService.getOnboardingStatus(guestUuid));
    }

    @Override
    @PostMapping("/onboarding/skip")
    public ApiResponse<OnboardingStatusResDto> skipOnboarding(
            @RequestHeader("guestUuid") String guestUuid
    ) {
        return ApiResponse.success(memberService.skipOnboarding(guestUuid));
    }
}
