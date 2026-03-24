package com.campusmarket.backend.domain.terms.controller;

import com.campusmarket.backend.domain.terms.dto.request.TermsAgreementCreateReqDto;
import com.campusmarket.backend.domain.terms.dto.response.MyTermsAgreementResDto;
import com.campusmarket.backend.domain.terms.dto.response.TermsAgreementSaveResDto;
import com.campusmarket.backend.domain.terms.dto.response.TermsListResDto;
import com.campusmarket.backend.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestHeader;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Terms", description = "약관 API")
public interface TermsControllerDocs {
    @Operation(summary = "이용 약관 조회", description = "이벤트용 약관 목록을 조회합니다.")
    ApiResponse<TermsListResDto> getTerms();

    @Operation(summary = "약관 동의 저장", description = "현재 회원의 약관 동의 여부를 저장합니다.")
    ApiResponse<TermsAgreementSaveResDto> saveAgreements(
            @Parameter(description = "게스트 UUID", required = true)
            @RequestHeader("guestUuid") String guestUuid,
            @Valid @RequestBody TermsAgreementCreateReqDto reqDto
    );

    @Operation(summary = "내 약관 동의 내역 조회", description = "현재 회원의 약관 동의 상태를 조회합니다.")
    ApiResponse<MyTermsAgreementResDto> getMyAgreements(
            @Parameter(description = "게스트 UUID", required = true)
            @RequestHeader("guestUuid") String guestUuid
    );
}
