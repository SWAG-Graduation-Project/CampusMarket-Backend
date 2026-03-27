package com.campusmarket.backend.domain.report.dto.response;

import com.campusmarket.backend.domain.report.constant.ReportStatus;
import com.campusmarket.backend.domain.report.entity.Report;

import java.time.LocalDateTime;

public record ReportResDto(
        Long reportId,
        ReportStatus status,
        LocalDateTime reportedAt
) {
    public static ReportResDto from(Report report) {
        return new ReportResDto(
                report.getId(),
                report.getStatus(),
                report.getReportedAt()
        );
    }
}
