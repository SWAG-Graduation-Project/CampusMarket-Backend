package com.campusmarket.backend.domain.member.controller;

import com.campusmarket.backend.domain.member.dto.response.NicknameCheckResDto;
import com.campusmarket.backend.domain.member.dto.response.RandomNicknameResDto;
import com.campusmarket.backend.domain.member.service.MemberService;
import com.campusmarket.backend.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
