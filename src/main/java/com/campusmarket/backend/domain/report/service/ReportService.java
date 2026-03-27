package com.campusmarket.backend.domain.report.service;

import com.campusmarket.backend.domain.chat.entity.ChatRoom;
import com.campusmarket.backend.domain.chat.repository.ChatRoomRepository;
import com.campusmarket.backend.domain.member.constant.MemberErrorCode;
import com.campusmarket.backend.domain.member.entity.Member;
import com.campusmarket.backend.domain.member.exception.MemberException;
import com.campusmarket.backend.domain.member.repository.MemberRepository;
import com.campusmarket.backend.domain.report.constant.ReportErrorCode;
import com.campusmarket.backend.domain.report.constant.ReportReasonType;
import com.campusmarket.backend.domain.report.dto.request.ReportCreateReqDto;
import com.campusmarket.backend.domain.report.dto.response.ReportResDto;
import com.campusmarket.backend.domain.report.entity.Report;
import com.campusmarket.backend.domain.report.exception.ReportException;
import com.campusmarket.backend.domain.report.repository.ReportRepository;
import com.campusmarket.backend.global.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final MailService mailService;

    @Transactional
    public ReportResDto createReport(String guestUuid, Long chatRoomId, ReportCreateReqDto reqDto) {
        Member reporter = getMemberByGuestUuid(guestUuid);

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ReportException(ReportErrorCode.REPORT_CHAT_ROOM_NOT_FOUND));

        if (!chatRoom.isParticipant(reporter.getId())) {
            throw new ReportException(ReportErrorCode.REPORT_FORBIDDEN);
        }

        if (reportRepository.existsByReporterIdAndChatRoomId(reporter.getId(), chatRoomId)) {
            throw new ReportException(ReportErrorCode.REPORT_DUPLICATE);
        }

        if (reqDto.reasonType() == ReportReasonType.OTHER &&
                (reqDto.reasonDetail() == null || reqDto.reasonDetail().isBlank())) {
            throw new ReportException(ReportErrorCode.REPORT_OTHER_DETAIL_REQUIRED);
        }

        Long reportedId = chatRoom.getOpponentId(reporter.getId());

        Report report = Report.builder()
                .reporterId(reporter.getId())
                .reportedId(reportedId)
                .chatRoomId(chatRoomId)
                .reasonType(reqDto.reasonType())
                .reasonDetail(reqDto.reasonDetail())
                .build();

        Report saved = reportRepository.save(report);
        mailService.sendReportNotification(saved);

        return ReportResDto.from(saved);
    }

    private Member getMemberByGuestUuid(String guestUuid) {
        return memberRepository.findByGuestUuid(guestUuid)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
