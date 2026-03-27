package com.campusmarket.backend.domain.chat.dto.response;

import com.campusmarket.backend.domain.chat.constant.ProposalStatus;
import com.campusmarket.backend.domain.chat.constant.ProposalType;
import com.campusmarket.backend.domain.chat.entity.TradeProposal;

import java.time.LocalDateTime;

public record TradeProposalResDto(
        Long proposalId,
        Long chatRoomId,
        ProposalType proposalType,
        ProposalStatus proposalStatus,
        Long proposalMessageId,
        Long responseMessageId,
        LocalDateTime proposedAt,
        LocalDateTime respondedAt
) {
    public static TradeProposalResDto from(TradeProposal proposal) {
        return new TradeProposalResDto(
                proposal.getId(),
                proposal.getChatRoomId(),
                proposal.getProposalType(),
                proposal.getProposalStatus(),
                proposal.getProposalMessageId(),
                proposal.getResponseMessageId(),
                proposal.getProposedAt(),
                proposal.getRespondedAt()
        );
    }
}
