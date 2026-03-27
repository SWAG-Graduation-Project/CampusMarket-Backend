package com.campusmarket.backend.domain.chat.dto.response;

import java.util.List;

public record ChatMessageListResDto(
        List<ChatMessageResDto> messages,
        int page,
        int size,
        long totalCount,
        boolean hasNext
) {}
