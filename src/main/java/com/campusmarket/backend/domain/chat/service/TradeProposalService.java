package com.campusmarket.backend.domain.chat.service;

import com.campusmarket.backend.domain.chat.constant.ChatErrorCode;
import com.campusmarket.backend.domain.chat.constant.MessageType;
import com.campusmarket.backend.domain.chat.constant.ProposalType;
import com.campusmarket.backend.domain.chat.dto.request.ProposalRespondReqDto;
import com.campusmarket.backend.domain.chat.dto.request.TradeProposalReqDto;
import com.campusmarket.backend.domain.chat.dto.response.ChatMessageResDto;
import com.campusmarket.backend.domain.chat.dto.response.TradeProposalResDto;
import com.campusmarket.backend.domain.chat.entity.ChatMessage;
import com.campusmarket.backend.domain.chat.entity.ChatRoom;
import com.campusmarket.backend.domain.chat.entity.TradeProposal;
import com.campusmarket.backend.domain.chat.exception.ChatException;
import com.campusmarket.backend.domain.chat.repository.ChatMessageRepository;
import com.campusmarket.backend.domain.chat.repository.ChatRoomRepository;
import com.campusmarket.backend.domain.chat.repository.TradeProposalRepository;
import com.campusmarket.backend.domain.member.constant.MemberErrorCode;
import com.campusmarket.backend.domain.member.entity.Member;
import com.campusmarket.backend.domain.member.exception.MemberException;
import com.campusmarket.backend.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TradeProposalService {

    private final TradeProposalRepository tradeProposalRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatSystemMessageService chatSystemMessageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    // 거래 제안 전송 - 판매자만 가능, PROPOSAL 메시지 저장 후 제안 레코드 생성
    @Transactional
    public TradeProposalResDto sendTradeProposal(String guestUuid, Long chatRoomId, TradeProposalReqDto reqDto) {
        Member sender = getMemberByGuestUuid(guestUuid);
        ChatRoom chatRoom = getChatRoomAndValidateParticipant(chatRoomId, sender.getId());

        // 판매자만 거래 제안 가능
        if (!chatRoom.getSellerId().equals(sender.getId())) {
            throw new ChatException(ChatErrorCode.TRADE_PROPOSAL_SELLER_ONLY);
        }

        ChatMessage proposalMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .senderId(sender.getId())
                .messageType(MessageType.PROPOSAL)
                .content(reqDto.proposalType().name() + " 거래를 제안했습니다.")
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(proposalMessage);
        chatRoom.updateLastMessage(savedMessage.getId(), savedMessage.getCreatedAt());

        TradeProposal proposal = TradeProposal.builder()
                .chatRoomId(chatRoomId)
                .proposalType(reqDto.proposalType())
                .proposalMessageId(savedMessage.getId())
                .build();

        TradeProposal savedProposal = tradeProposalRepository.save(proposal);

        try {
            String metadata = objectMapper.writeValueAsString(Map.of(
                    "proposalId", savedProposal.getId(),
                    "proposalType", reqDto.proposalType().name()
            ));
            savedMessage.updateMetadata(metadata);
        } catch (Exception e) {
            log.warn("제안 메시지 메타데이터 직렬화 실패 - chatRoomId={}", chatRoomId, e);
        }

        messagingTemplate.convertAndSend("/sub/chat/" + chatRoomId,
                ChatMessageResDto.of(savedMessage, sender.getNickname()));

        return TradeProposalResDto.from(savedProposal);
    }

    // 거래 제안 수락/거절 - 구매자만 가능, 수락 시 사물함/시간표 자동 공유 트리거
    @Transactional
    public TradeProposalResDto respondToProposal(String guestUuid, Long chatRoomId, Long proposalId, ProposalRespondReqDto reqDto) {
        Member responder = getMemberByGuestUuid(guestUuid);
        ChatRoom chatRoom = getChatRoomAndValidateParticipant(chatRoomId, responder.getId());

        TradeProposal proposal = tradeProposalRepository.findByIdAndChatRoomId(proposalId, chatRoomId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.TRADE_PROPOSAL_NOT_FOUND));

        // 구매자만 수락/거절 가능
        if (!chatRoom.getBuyerId().equals(responder.getId())) {
            throw new ChatException(ChatErrorCode.TRADE_PROPOSAL_FORBIDDEN);
        }

        if (!proposal.isPending()) {
            throw new ChatException(ChatErrorCode.TRADE_PROPOSAL_ALREADY_RESPONDED);
        }

        String responseText = Boolean.TRUE.equals(reqDto.accept()) ? "거래 제안을 수락했습니다." : "거래 제안을 거절했습니다.";

        String responseMetadata = null;
        try {
            responseMetadata = objectMapper.writeValueAsString(Map.of(
                    "proposalId", proposalId,
                    "accept", Boolean.TRUE.equals(reqDto.accept())
            ));
        } catch (Exception e) {
            log.warn("응답 메시지 메타데이터 직렬화 실패 - proposalId={}", proposalId, e);
        }

        ChatMessage responseMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .senderId(responder.getId())
                .messageType(MessageType.PROPOSAL)
                .content(responseText)
                .metadata(responseMetadata)
                .build();

        ChatMessage savedResponse = chatMessageRepository.save(responseMessage);
        chatRoom.updateLastMessage(savedResponse.getId(), savedResponse.getCreatedAt());

        messagingTemplate.convertAndSend("/sub/chat/" + chatRoomId,
                ChatMessageResDto.of(savedResponse, responder.getNickname()));

        if (Boolean.TRUE.equals(reqDto.accept())) {
            proposal.accept(savedResponse.getId());
            sendShareMessages(chatRoom, proposal.getProposalType());
        } else {
            proposal.reject(savedResponse.getId());
        }

        return TradeProposalResDto.from(proposal);
    }

    // 거래 유형에 따라 사물함/시간표 공유 메시지 전송 위임
    private void sendShareMessages(ChatRoom chatRoom, ProposalType proposalType) {
        List<Member> participants = memberRepository.findAllById(
                List.of(chatRoom.getSellerId(), chatRoom.getBuyerId())
        );

        Member seller = participants.stream()
                .filter(m -> m.getId().equals(chatRoom.getSellerId()))
                .findFirst().orElse(null);
        Member buyer = participants.stream()
                .filter(m -> m.getId().equals(chatRoom.getBuyerId()))
                .findFirst().orElse(null);

        if (proposalType == ProposalType.LOCKER) {
            chatSystemMessageService.sendLockerShareMessages(chatRoom, seller, buyer);
        } else if (proposalType == ProposalType.FACE_TO_FACE) {
            chatSystemMessageService.sendTimetableShareMessages(chatRoom, seller, buyer);
        }
    }

    private ChatRoom getChatRoomAndValidateParticipant(Long chatRoomId, Long memberId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));
        if (!chatRoom.isParticipant(memberId)) {
            throw new ChatException(ChatErrorCode.CHAT_ROOM_FORBIDDEN);
        }
        return chatRoom;
    }

    private Member getMemberByGuestUuid(String guestUuid) {
        Member member = memberRepository.findByGuestUuid(guestUuid)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        if (member.isWithdrawn()) {
            throw new MemberException(MemberErrorCode.MEMBER_WITHDRAWN);
        }
        return member;
    }
}
