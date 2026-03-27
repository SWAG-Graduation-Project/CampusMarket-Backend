package com.campusmarket.backend.domain.chat.constant;

public enum MessageType {
    TEXT,           // 텍스트
    IMAGE,          // 이미지 URL (content에 URL 담아서 전송)
    SYSTEM,         // 시스템 메시지
    PROPOSAL,       // 거래 제안
    LOCKER_SHARE,   // 사물함 위치 공유
    TIMETABLE_SHARE // 시간표 공유
}
