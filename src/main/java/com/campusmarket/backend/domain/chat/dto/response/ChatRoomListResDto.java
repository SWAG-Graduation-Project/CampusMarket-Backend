package com.campusmarket.backend.domain.chat.dto.response;

import java.util.List;

public record ChatRoomListResDto(
        List<ChatRoomSummaryResDto> chatRooms
) {}
