package com.campusmarket.backend.domain.chat.dto.response;

import com.campusmarket.backend.domain.chat.constant.ChatRoomStatus;

import java.time.LocalDateTime;

public record ChatRoomSummaryResDto(
        Long chatRoomId,
        Long productId,
        String productName,
        String productThumbnailUrl,
        boolean isSeller,
        Long sellerId,
        String sellerNickname,
        String sellerProfileImageUrl,
        Long buyerId,
        String buyerNickname,
        String buyerProfileImageUrl,
        String lastMessageContent,
        LocalDateTime lastMessageAt,
        LocalDateTime createdAt,
        ChatRoomStatus status
) {}
