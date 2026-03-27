package com.campusmarket.backend.domain.report.entity;

import com.campusmarket.backend.domain.report.constant.ReportReasonType;
import com.campusmarket.backend.domain.report.constant.ReportStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "신고")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "신고PK")
    private Long id;

    @Column(name = "신고자ID", nullable = false)
    private Long reporterId;

    @Column(name = "피신고자ID", nullable = false)
    private Long reportedId;

    @Column(name = "채팅방ID")
    private Long chatRoomId;

    @Enumerated(EnumType.STRING)
    @Column(name = "신고사유유형", length = 30)
    private ReportReasonType reasonType;

    @Column(name = "기타사유상세", length = 500)
    private String reasonDetail;

    @Enumerated(EnumType.STRING)
    @Column(name = "처리상태", length = 20)
    private ReportStatus status;

    @Column(name = "신고일")
    private LocalDateTime reportedAt;

    @Builder
    private Report(Long reporterId, Long reportedId, Long chatRoomId, ReportReasonType reasonType, String reasonDetail) {
        this.reporterId = reporterId;
        this.reportedId = reportedId;
        this.chatRoomId = chatRoomId;
        this.reasonType = reasonType;
        this.reasonDetail = reasonDetail;
        this.status = ReportStatus.PENDING;
        this.reportedAt = LocalDateTime.now();
    }
}
