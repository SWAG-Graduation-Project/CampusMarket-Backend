package com.campusmarket.backend.domain.chat.dto.request;

import com.campusmarket.backend.domain.chat.constant.MessageType;
import jakarta.validation.constraints.NotNull;

public record SendMessageReqDto(
        @NotNull(message = "guestUuid는 필수입니다.")
        String guestUuid,

        @NotNull(message = "메시지 유형은 필수입니다.")
        MessageType messageType,

        String content,

        String metadata
) {}
