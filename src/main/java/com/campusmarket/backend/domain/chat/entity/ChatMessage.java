package com.campusmarket.backend.domain.chat.entity;

import com.campusmarket.backend.domain.chat.constant.MessageType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "채팅메시지")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "메시지PK")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "채팅방ID", nullable = false)
    private ChatRoom chatRoom;

    @Column(name = "보낸사람ID")
    private Long senderId;  // 시스템 메시지는 null

    @Enumerated(EnumType.STRING)
    @Column(name = "메시지유형", length = 30, nullable = false)
    private MessageType messageType;

    @Column(name = "메시지내용", columnDefinition = "TEXT")
    private String content;

    @Column(name = "메타데이터", columnDefinition = "JSON")
    private String metadata;

    @Column(name = "생성일", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "삭제일")
    private LocalDateTime deletedAt;

    @Builder
    private ChatMessage(ChatRoom chatRoom, Long senderId, MessageType messageType, String content, String metadata) {
        this.chatRoom = chatRoom;
        this.senderId = senderId;
        this.messageType = messageType;
        this.content = content;
        this.metadata = metadata;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
