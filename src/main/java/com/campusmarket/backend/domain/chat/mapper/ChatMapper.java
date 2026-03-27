package com.campusmarket.backend.domain.chat.mapper;

import com.campusmarket.backend.domain.chat.dto.response.ChatRoomSummaryResDto;
import com.campusmarket.backend.domain.chat.entity.ChatMessage;
import com.campusmarket.backend.domain.chat.entity.ChatRoom;
import com.campusmarket.backend.domain.member.entity.Member;
import com.campusmarket.backend.domain.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {

    public ChatRoomSummaryResDto toChatRoomSummaryResDto(
            ChatRoom chatRoom,
            Long currentMemberId,
            Member seller,
            Member buyer,
            Product product,
            String productThumbnailUrl,
            ChatMessage lastMessage
    ) {
        return new ChatRoomSummaryResDto(
                chatRoom.getId(),
                chatRoom.getProductId(),
                product != null ? product.getName() : null,
                productThumbnailUrl,
                chatRoom.getSellerId().equals(currentMemberId),
                seller != null ? seller.getId() : null,
                seller != null ? seller.getNickname() : null,
                seller != null ? seller.getProfileImageUrl() : null,
                buyer != null ? buyer.getId() : null,
                buyer != null ? buyer.getNickname() : null,
                buyer != null ? buyer.getProfileImageUrl() : null,
                lastMessage != null && !lastMessage.isDeleted() ? lastMessage.getContent() : null,
                chatRoom.getLastMessageAt(),
                chatRoom.getCreatedAt(),
                chatRoom.getStatus()
        );
    }
}
