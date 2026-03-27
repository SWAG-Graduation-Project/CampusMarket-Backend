package com.campusmarket.backend.domain.chat.constant;

import com.campusmarket.backend.global.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements BaseErrorCode {

    // 채팅방
    CHAT_ROOM_NOT_FOUND("CHAT_001", "채팅방을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CHAT_ROOM_FORBIDDEN("CHAT_002", "해당 채팅방에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    CHAT_ROOM_ALREADY_DELETED("CHAT_003", "이미 나간 채팅방입니다.", HttpStatus.BAD_REQUEST),
    CHAT_CANNOT_SELF("CHAT_004", "본인의 상품에는 채팅을 걸 수 없습니다.", HttpStatus.BAD_REQUEST),

    // 거래 제안
    TRADE_PROPOSAL_NOT_FOUND("CHAT_005", "거래 제안을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TRADE_PROPOSAL_ALREADY_RESPONDED("CHAT_006", "이미 응답한 거래 제안입니다.", HttpStatus.BAD_REQUEST),
    TRADE_PROPOSAL_FORBIDDEN("CHAT_007", "거래 제안에 응답할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    TRADE_PROPOSAL_SELLER_ONLY("CHAT_012", "거래 제안은 판매자만 할 수 있습니다.", HttpStatus.FORBIDDEN),

    // 차단
    ALREADY_BLOCKED("CHAT_008", "이미 차단한 상대입니다.", HttpStatus.BAD_REQUEST),
    BLOCKED_USER("CHAT_009", "차단된 사용자와는 채팅할 수 없습니다.", HttpStatus.FORBIDDEN),
    BLOCK_NOT_FOUND("CHAT_010", "차단 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CANNOT_BLOCK_SELF("CHAT_011", "자기 자신을 차단할 수 없습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
