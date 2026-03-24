package com.campusmarket.backend.domain.terms.controller;

import com.campusmarket.backend.domain.terms.dto.request.TermsAgreementCreateReqDto;
import com.campusmarket.backend.domain.terms.dto.response.MyTermsAgreementResDto;
import com.campusmarket.backend.domain.terms.dto.response.TermsAgreementSaveResDto;
import com.campusmarket.backend.domain.terms.dto.response.TermsListResDto;
import com.campusmarket.backend.domain.terms.service.TermsService;
import com.campusmarket.backend.global.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/terms")
public class TermsController implements TermsControllerDocs {

    private final TermsService termsService;

    @Override
    @GetMapping
    public ApiResponse<TermsListResDto> getTerms() {
        return ApiResponse.success(termsService.getTerms());
    }

    @Override
    @PostMapping("/agreements")
    public ApiResponse<TermsAgreementSaveResDto> saveAgreements(
            @RequestHeader("guestUuid") String guestUuid,
            @Valid @RequestBody TermsAgreementCreateReqDto reqDto
    ) {
        return ApiResponse.success(termsService.saveAgreements(guestUuid, reqDto));
    }

    @Override
    @GetMapping("/agreements/me")
    public ApiResponse<MyTermsAgreementResDto> getMyAgreements(
            @RequestHeader("guestUuid") String guestUuid
    ) {
        return ApiResponse.success(termsService.getMyAgreements(guestUuid));
    }
}