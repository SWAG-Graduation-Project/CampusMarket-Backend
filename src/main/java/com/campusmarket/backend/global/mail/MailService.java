package com.campusmarket.backend.global.mail;

import com.campusmarket.backend.domain.report.entity.Report;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.admin-email}")
    private String adminEmail;

    /**
     * 신고 접수 시 운영자에게 이메일 발송 (@Async — 비동기 처리)
     * 발송 실패해도 로그만 남기고 사용자 응답에 영향 없음
     */
    @Async
    public void sendReportNotification(Report report) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(adminEmail);
            message.setSubject("[CampusMarket] 신고 접수 - 신고번호 " + report.getId());
            message.setText(buildReportMailBody(report));
            mailSender.send(message);
        } catch (Exception e) {
            log.error("신고 이메일 발송 실패. reportId={}", report.getId(), e);
        }
    }

    private String buildReportMailBody(Report report) {
        return """
                신고 접수

                신고 번호 : %d
                신고자 ID : %d
                피신고자 ID: %d
                채팅방 ID : %d
                신고 사유 : %s
                기타 상세 : %s
                신고 일시 : %s
                """.formatted(
                report.getId(),
                report.getReporterId(),
                report.getReportedId(),
                report.getChatRoomId(),
                report.getReasonType().name(),
                report.getReasonDetail() != null ? report.getReasonDetail() : "-",
                report.getReportedAt()
        );
    }
}
