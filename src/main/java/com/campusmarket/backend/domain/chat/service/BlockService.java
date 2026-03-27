package com.campusmarket.backend.domain.chat.service;

import com.campusmarket.backend.domain.chat.constant.ChatErrorCode;
import com.campusmarket.backend.domain.chat.entity.Block;
import com.campusmarket.backend.domain.chat.entity.ChatRoom;
import com.campusmarket.backend.domain.chat.exception.ChatException;
import com.campusmarket.backend.domain.chat.repository.BlockRepository;
import com.campusmarket.backend.domain.chat.repository.ChatRoomRepository;
import com.campusmarket.backend.domain.member.constant.MemberErrorCode;
import com.campusmarket.backend.domain.member.entity.Member;
import com.campusmarket.backend.domain.member.exception.MemberException;
import com.campusmarket.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockService {

    private final BlockRepository blockRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    // 채팅방 상대방 차단 - 이후 해당 유저의 내 상품 채팅방 진입 불가
    @Transactional
    public void blockUser(String guestUuid, Long chatRoomId) {
        Member blocker = getMemberByGuestUuid(guestUuid);
        ChatRoom chatRoom = getChatRoomAndValidateParticipant(chatRoomId, blocker.getId());

        Long targetId = chatRoom.getOpponentId(blocker.getId());

        if (blocker.getId().equals(targetId)) {
            throw new ChatException(ChatErrorCode.CANNOT_BLOCK_SELF);
        }

        if (blockRepository.existsByMemberIdAndBlockedId(blocker.getId(), targetId)) {
            throw new ChatException(ChatErrorCode.ALREADY_BLOCKED);
        }

        blockRepository.save(Block.builder()
                .memberId(blocker.getId())
                .blockedId(targetId)
                .build());
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
        return memberRepository.findByGuestUuid(guestUuid)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
