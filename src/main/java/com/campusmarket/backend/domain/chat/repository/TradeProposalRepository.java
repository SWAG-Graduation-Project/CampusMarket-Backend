package com.campusmarket.backend.domain.chat.repository;

import com.campusmarket.backend.domain.chat.entity.TradeProposal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TradeProposalRepository extends JpaRepository<TradeProposal, Long> {

    Optional<TradeProposal> findByIdAndChatRoomId(Long id, Long chatRoomId);
}
