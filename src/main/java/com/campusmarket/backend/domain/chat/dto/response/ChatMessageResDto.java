package com.campusmarket.backend.domain.chat.dto.response;

import com.campusmarket.backend.domain.chat.constant.MessageType;
import com.campusmarket.backend.domain.chat.entity.ChatMessage;

import java.time.LocalDateTime;

public record ChatMessageResDto(
        Long messageId,
        Long senderId,
        String senderNickname,
        MessageType messageType,
        String content,
        String metadata,
        LocalDateTime createdAt,
        boolean isDeleted
) {
    public static ChatMessageResDto of(ChatMessage message, String senderNickname) {
        return new ChatMessageResDto(
                message.getId(),
                message.getSenderId(),
                senderNickname,
                message.getMessageType(),
                message.isDeleted() ? null : message.getContent(),
                message.isDeleted() ? null : message.getMetadata(),
                message.getCreatedAt(),
                message.isDeleted()
        );
    }
}
