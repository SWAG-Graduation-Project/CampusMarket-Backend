package com.campusmarket.backend.domain.chat.service;

import com.campusmarket.backend.domain.chat.constant.ChatErrorCode;
import com.campusmarket.backend.domain.chat.constant.MessageType;
import com.campusmarket.backend.domain.chat.dto.request.SendMessageReqDto;
import com.campusmarket.backend.domain.chat.dto.response.ChatMessageListResDto;
import com.campusmarket.backend.domain.chat.dto.response.ChatMessageResDto;
import com.campusmarket.backend.domain.chat.entity.ChatMessage;
import com.campusmarket.backend.domain.chat.entity.ChatRoom;
import com.campusmarket.backend.domain.chat.exception.ChatException;
import com.campusmarket.backend.domain.chat.repository.ChatMessageRepository;
import com.campusmarket.backend.domain.chat.repository.ChatRoomRepository;
import com.campusmarket.backend.domain.member.constant.MemberErrorCode;
import com.campusmarket.backend.domain.member.entity.Member;
import com.campusmarket.backend.domain.member.exception.MemberException;
import com.campusmarket.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    // 채팅방 메시지 내역 페이지 조회 - 발신자 닉네임 배치 조회 포함
    public ChatMessageListResDto getMessageList(String guestUuid, Long chatRoomId, int page, int size) {
        Member member = getMemberByGuestUuid(guestUuid);
        getChatRoomAndValidateParticipant(chatRoomId, member.getId());

        List<ChatMessage> messages = chatMessageRepository.findByChatRoomId(
                chatRoomId, PageRequest.of(page, size)
        );
        long totalCount = chatMessageRepository.countByChatRoomId(chatRoomId);

        List<Long> senderIds = messages.stream()
                .map(ChatMessage::getSenderId)
                .filter(id -> id != null)
                .distinct()
                .toList();

        Map<Long, String> nicknameMap = memberRepository.findAllById(senderIds)
                .stream()
                .collect(Collectors.toMap(
                        Member::getId,
                        m -> m.getNickname() != null ? m.getNickname() : "알 수 없음"
                ));

        return new ChatMessageListResDto(
                messages.stream()
                        .map(m -> ChatMessageResDto.of(m, nicknameMap.getOrDefault(m.getSenderId(), "알 수 없음")))
                        .toList(),
                page,
                size,
                totalCount,
                (long) (page + 1) * size < totalCount
        );
    }

    // WebSocket으로 메시지 전송 - 탈퇴 사용자 차단, DB 저장 후 STOMP 브로드캐스트용 응답 반환
    @Transactional
    public ChatMessageResDto sendMessage(Long chatRoomId, SendMessageReqDto reqDto) {
        Member sender = getMemberByGuestUuid(reqDto.guestUuid());
        ChatRoom chatRoom = getChatRoomAndValidateParticipant(chatRoomId, sender.getId());

        // 상대방 탈퇴 여부 확인 - 탈퇴 시 시스템 메시지 반환, 원본 메시지 저장 X
        Long otherMemberId = sender.getId().equals(chatRoom.getSellerId())
                ? chatRoom.getBuyerId()
                : chatRoom.getSellerId();

        Member otherMember = memberRepository.findById(otherMemberId).orElse(null);
        if (otherMember != null && otherMember.isWithdrawn()) {
            throw new ChatException(ChatErrorCode.CHAT_WITHDRAWN_USER);
        }

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .senderId(sender.getId())
                .messageType(reqDto.messageType())
                .content(reqDto.content())
                .metadata(reqDto.metadata())
                .build();

        ChatMessage saved = chatMessageRepository.save(message);
        chatRoom.updateLastMessage(saved.getId(), saved.getCreatedAt());

        return ChatMessageResDto.of(saved, sender.getNickname());
    }

    // 이미지 메시지 저장 - 채팅방 이미지 업로드 REST API에서 호출
    @Transactional
    public ChatMessageResDto saveImageMessage(ChatRoom chatRoom, Long senderId, String imageUrl, String senderNickname) {
        Long otherMemberId = senderId.equals(chatRoom.getSellerId())
                ? chatRoom.getBuyerId()
                : chatRoom.getSellerId();

        Member otherMember = memberRepository.findById(otherMemberId).orElse(null);
        if (otherMember != null && otherMember.isWithdrawn()) {
            throw new ChatException(ChatErrorCode.CHAT_WITHDRAWN_USER);
        }

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .senderId(senderId)
                .messageType(MessageType.IMAGE)
                .content(imageUrl)
                .build();

        ChatMessage saved = chatMessageRepository.save(message);
        chatRoom.updateLastMessage(saved.getId(), saved.getCreatedAt());

        return ChatMessageResDto.of(saved, senderNickname);
    }

    private ChatMessageResDto saveSystemMessage(ChatRoom chatRoom, String content) {
        ChatMessage systemMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .senderId(null)
                .messageType(MessageType.SYSTEM)
                .content(content)
                .build();
        ChatMessage saved = chatMessageRepository.save(systemMessage);
        chatRoom.updateLastMessage(saved.getId(), saved.getCreatedAt());
        return ChatMessageResDto.of(saved, "알 수 없음");
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
