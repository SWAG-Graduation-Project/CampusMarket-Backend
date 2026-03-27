package com.campusmarket.backend.domain.report.controller;

import com.campusmarket.backend.domain.report.dto.request.ReportCreateReqDto;
import com.campusmarket.backend.domain.report.dto.response.ReportResDto;
import com.campusmarket.backend.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Report", description = "신고 API")
public interface ReportControllerDocs {

    @Operation(summary = "채팅방 신고", description = "채팅방 상대방을 신고. 동일 채팅방 중복 신고 불가.")
    ApiResponse<ReportResDto> createReport(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid,
            @PathVariable Long chatRoomId,
            @Valid @RequestBody ReportCreateReqDto reqDto
    );
}
