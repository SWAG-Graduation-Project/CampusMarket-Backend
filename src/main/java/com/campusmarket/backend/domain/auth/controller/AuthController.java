package com.campusmarket.backend.domain.auth.controller;

import com.campusmarket.backend.domain.auth.dto.CurrentMemberInfoRespDto;
import com.campusmarket.backend.domain.auth.dto.GuestCreateReqDto;
import com.campusmarket.backend.domain.auth.dto.GuestCreateRespDto;
import com.campusmarket.backend.domain.auth.service.AuthService;
import com.campusmarket.backend.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs{

    private final AuthService authService;

    @Override
    public ApiResponse<GuestCreateRespDto> createGuestMember(GuestCreateReqDto reqDto){
        return ApiResponse.success(authService.createGuestMember(reqDto));
    }

    @Override
    public ApiResponse<CurrentMemberInfoRespDto> getCurrentMemberInfo(String guestUuid) {
        return ApiResponse.success(authService.getCurrentMemberInfo(guestUuid));
    }
}
