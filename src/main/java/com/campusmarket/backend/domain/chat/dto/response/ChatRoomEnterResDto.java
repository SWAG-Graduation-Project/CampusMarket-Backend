package com.campusmarket.backend.domain.chat.dto.response;

public record ChatRoomEnterResDto(
        Long chatRoomId,
        boolean isNew
) {}
