package com.campusmarket.backend.domain.chat.entity;

import com.campusmarket.backend.domain.chat.constant.ProposalStatus;
import com.campusmarket.backend.domain.chat.constant.ProposalType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "거래제안")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradeProposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "거래제안PK")
    private Long id;

    @Column(name = "채팅방ID", nullable = false)
    private Long chatRoomId;

    @Enumerated(EnumType.STRING)
    @Column(name = "제안유형", length = 20)
    private ProposalType proposalType;

    @Enumerated(EnumType.STRING)
    @Column(name = "제안상태", length = 20)
    private ProposalStatus proposalStatus;

    @Column(name = "제안메시지ID")
    private Long proposalMessageId;

    @Column(name = "응답메시지ID")
    private Long responseMessageId;

    @Column(name = "제안일")
    private LocalDateTime proposedAt;

    @Column(name = "응답일")
    private LocalDateTime respondedAt;

    @Builder
    private TradeProposal(Long chatRoomId, ProposalType proposalType, Long proposalMessageId) {
        this.chatRoomId = chatRoomId;
        this.proposalType = proposalType;
        this.proposalStatus = ProposalStatus.PENDING;
        this.proposalMessageId = proposalMessageId;
        this.proposedAt = LocalDateTime.now();
    }

    public boolean isPending() {
        return this.proposalStatus == ProposalStatus.PENDING;
    }

    public void accept(Long responseMessageId) {
        this.proposalStatus = ProposalStatus.ACCEPTED;
        this.responseMessageId = responseMessageId;
        this.respondedAt = LocalDateTime.now();
    }

    public void reject(Long responseMessageId) {
        this.proposalStatus = ProposalStatus.REJECTED;
        this.responseMessageId = responseMessageId;
        this.respondedAt = LocalDateTime.now();
    }
}
