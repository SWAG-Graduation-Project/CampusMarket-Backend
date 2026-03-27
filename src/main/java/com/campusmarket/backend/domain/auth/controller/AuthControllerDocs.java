package com.campusmarket.backend.domain.auth.controller;

import com.campusmarket.backend.domain.auth.dto.CurrentMemberInfoRespDto;
import com.campusmarket.backend.domain.auth.dto.GuestCreateReqDto;
import com.campusmarket.backend.domain.auth.dto.GuestCreateRespDto;
import com.campusmarket.backend.global.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
public interface AuthControllerDocs {

    @PostMapping("/guest")
    ApiResponse<GuestCreateRespDto> createGuestMember(
            @Valid @RequestBody GuestCreateReqDto reqDto
    );

    @GetMapping("/me")
    ApiResponse<CurrentMemberInfoRespDto> getCurrentMemberInfo(
            @RequestHeader("X-Guest-UUID") String guestUuid
    );
}
