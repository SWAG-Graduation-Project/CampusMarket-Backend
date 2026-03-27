package com.campusmarket.backend.domain.chat.entity;

import com.campusmarket.backend.domain.chat.constant.ChatRoomStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "채팅방")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "채팅방PK")
    private Long id;

    @Column(name = "상품ID", nullable = false)
    private Long productId;

    @Column(name = "판매자ID", nullable = false)
    private Long sellerId;

    @Column(name = "구매자ID", nullable = false)
    private Long buyerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "채팅방상태", nullable = false, length = 20)
    private ChatRoomStatus status;

    @Column(name = "마지막메시지ID")
    private Long lastMessageId;

    @Column(name = "마지막메시지시각")
    private LocalDateTime lastMessageAt;

    @Column(name = "생성일")
    private LocalDateTime createdAt;

    @Column(name = "수정일")
    private LocalDateTime updatedAt;

    @Builder
    private ChatRoom(Long productId, Long sellerId, Long buyerId) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.status = ChatRoomStatus.ACTIVE;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateLastMessage(Long messageId, LocalDateTime messageAt) {
        this.lastMessageId = messageId;
        this.lastMessageAt = messageAt;
    }

    public void leave() {
        this.status = ChatRoomStatus.DELETED;
    }

    public void markAsSold() {
        this.status = ChatRoomStatus.SOLD;
    }

    public boolean isParticipant(Long memberId) {
        return sellerId.equals(memberId) || buyerId.equals(memberId);
    }

    public Long getOpponentId(Long memberId) {
        return sellerId.equals(memberId) ? buyerId : sellerId;
    }
}
