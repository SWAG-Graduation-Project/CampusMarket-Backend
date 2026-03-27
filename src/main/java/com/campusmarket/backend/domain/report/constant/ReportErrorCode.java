package com.campusmarket.backend.domain.report.constant;

import com.campusmarket.backend.global.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReportErrorCode implements BaseErrorCode {

    REPORT_DUPLICATE("REPORT_001", "이미 신고한 채팅방입니다.", HttpStatus.BAD_REQUEST),
    REPORT_CHAT_ROOM_NOT_FOUND("REPORT_002", "신고할 채팅방을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    REPORT_FORBIDDEN("REPORT_003", "해당 채팅방의 참여자만 신고할 수 있습니다.", HttpStatus.FORBIDDEN),
    REPORT_CANNOT_SELF("REPORT_004", "자기 자신을 신고할 수 없습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
