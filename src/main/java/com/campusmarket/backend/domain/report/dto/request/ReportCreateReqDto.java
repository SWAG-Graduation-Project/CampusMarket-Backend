package com.campusmarket.backend.domain.report.dto.request;

import com.campusmarket.backend.domain.report.constant.ReportReasonType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportCreateReqDto(
        @NotNull(message = "신고 사유는 필수입니다.")
        ReportReasonType reasonType,

        @Size(max = 500, message = "기타 사유는 500자 이하로 입력해주세요.")
        String reasonDetail
) {}
