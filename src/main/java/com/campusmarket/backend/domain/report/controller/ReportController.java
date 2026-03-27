package com.campusmarket.backend.domain.report.controller;

import com.campusmarket.backend.domain.report.dto.request.ReportCreateReqDto;
import com.campusmarket.backend.domain.report.dto.response.ReportResDto;
import com.campusmarket.backend.domain.report.service.ReportService;
import com.campusmarket.backend.global.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/rooms")
public class ReportController implements ReportControllerDocs {

    private final ReportService reportService;

    @Override
    @PostMapping("/{chatRoomId}/report")
    public ApiResponse<ReportResDto> createReport(
            @RequestHeader("guestUuid") String guestUuid,
            @PathVariable Long chatRoomId,
            @Valid @RequestBody ReportCreateReqDto reqDto
    ) {
        return ApiResponse.success(reportService.createReport(guestUuid, chatRoomId, reqDto));
    }
}
