package com.campusmarket.backend.domain.auth.controller;

import com.campusmarket.backend.domain.auth.dto.CurrentMemberInfoRespDto;
import com.campusmarket.backend.domain.auth.dto.GuestCreateReqDto;
import com.campusmarket.backend.domain.auth.dto.GuestCreateRespDto;
import com.campusmarket.backend.domain.auth.service.AuthService;
import com.campusmarket.backend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs{

    private final AuthService authService;

    @Override
    public BaseResponse<GuestCreateRespDto> createGuestMember(GuestCreateReqDto reqDto){
        return BaseResponse.onSuccess(authService.createGuestMember(reqDto));
    }

    @Override
    public BaseResponse<CurrentMemberInfoRespDto> getCurrentMemberInfo(String guestUuid) {
        return BaseResponse.onSuccess(authService.getCurrentMemberInfo(guestUuid));
    }
}
