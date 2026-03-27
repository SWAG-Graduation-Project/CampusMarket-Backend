package com.campusmarket.backend.domain.chat.service;

import com.campusmarket.backend.domain.chat.constant.ChatErrorCode;
import com.campusmarket.backend.domain.chat.constant.ChatRoomStatus;
import com.campusmarket.backend.domain.chat.dto.request.ChatRoomCreateReqDto;
import com.campusmarket.backend.domain.chat.dto.response.ChatRoomEnterResDto;
import com.campusmarket.backend.domain.chat.dto.response.ChatRoomListResDto;
import com.campusmarket.backend.domain.chat.dto.response.ChatRoomSummaryResDto;
import com.campusmarket.backend.domain.chat.entity.ChatMessage;
import com.campusmarket.backend.domain.chat.entity.ChatRoom;
import com.campusmarket.backend.domain.chat.exception.ChatException;
import com.campusmarket.backend.domain.chat.mapper.ChatMapper;
import com.campusmarket.backend.domain.chat.repository.BlockRepository;
import com.campusmarket.backend.domain.chat.repository.ChatMessageRepository;
import com.campusmarket.backend.domain.chat.repository.ChatRoomRepository;
import com.campusmarket.backend.domain.member.constant.MemberErrorCode;
import com.campusmarket.backend.domain.member.entity.Member;
import com.campusmarket.backend.domain.member.exception.MemberException;
import com.campusmarket.backend.domain.member.repository.MemberRepository;
import com.campusmarket.backend.domain.product.entity.Product;
import com.campusmarket.backend.domain.product.entity.ProductImage;
import com.campusmarket.backend.domain.product.entity.ProductSaleStatus;
import com.campusmarket.backend.domain.product.exception.ProductErrorCode;
import com.campusmarket.backend.domain.product.exception.ProductException;
import com.campusmarket.backend.domain.product.repository.ProductImageRepository;
import com.campusmarket.backend.domain.product.repository.ProductRepository;
import com.campusmarket.backend.domain.chat.dto.response.ChatMessageResDto;
import com.campusmarket.backend.global.file.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final BlockRepository blockRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ChatMapper chatMapper;
    private final FileStorageService fileStorageService;
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    // 채팅방 입장 - 기존 방이 있으면 반환, 없으면 신규 생성
    @Transactional
    public ChatRoomEnterResDto enterChatRoom(String guestUuid, ChatRoomCreateReqDto reqDto) {
        Member buyer = getMemberByGuestUuid(guestUuid);
        Product product = productRepository.findByIdAndSaleStatusNot(reqDto.productId(), ProductSaleStatus.DELETED)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        if (product.getSellerId().equals(buyer.getId())) {
            throw new ChatException(ChatErrorCode.CHAT_CANNOT_SELF);
        }

        if (blockRepository.isBlocked(buyer.getId(), product.getSellerId())) {
            throw new ChatException(ChatErrorCode.BLOCKED_USER);
        }

        return chatRoomRepository.findByProductIdAndBuyerId(reqDto.productId(), buyer.getId())
                .map(existing -> new ChatRoomEnterResDto(existing.getId(), false))
                .orElseGet(() -> {
                    ChatRoom newRoom = ChatRoom.builder()
                            .productId(product.getId())
                            .sellerId(product.getSellerId())
                            .buyerId(buyer.getId())
                            .build();
                    return new ChatRoomEnterResDto(chatRoomRepository.save(newRoom).getId(), true);
                });
    }

    // 내가 판매자인 채팅방 목록 조회 (최신 메시지 순, 나간 방 제외)
    public ChatRoomListResDto getSellingChatRoomList(String guestUuid) {
        Member member = getMemberByGuestUuid(guestUuid);
        List<ChatRoom> chatRooms = chatRoomRepository.findAllBySellerIdAndStatusNot(
                member.getId(), ChatRoomStatus.DELETED
        );
        return buildChatRoomList(chatRooms, member.getId());
    }

    // 내가 구매자인 채팅방 목록 조회 (최신 메시지 순, 나간 방 제외)
    public ChatRoomListResDto getBuyingChatRoomList(String guestUuid) {
        Member member = getMemberByGuestUuid(guestUuid);
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByBuyerIdAndStatusNot(
                member.getId(), ChatRoomStatus.DELETED
        );
        return buildChatRoomList(chatRooms, member.getId());
    }

    // 채팅 이미지 업로드 - 참여자 검증 후 S3 업로드, IMAGE 메시지 저장 및 WebSocket 브로드캐스트
    @Transactional
    public ChatMessageResDto uploadChatImage(String guestUuid, Long chatRoomId, MultipartFile file) {
        Member member = getMemberByGuestUuid(guestUuid);
        ChatRoom chatRoom = getChatRoomAndValidateParticipant(chatRoomId, member.getId());
        String imageUrl = fileStorageService.upload(file, "chats/" + chatRoomId).fileUrl();
        ChatMessageResDto messageDto = chatMessageService.saveImageMessage(
                chatRoom, member.getId(), imageUrl, member.getNickname()
        );
        messagingTemplate.convertAndSend("/sub/chat/" + chatRoomId, messageDto);
        return messageDto;
    }

    // 채팅방 나가기 - 상태를 DELETED로 변경
    @Transactional
    public void leaveChatRoom(String guestUuid, Long chatRoomId) {
        Member member = getMemberByGuestUuid(guestUuid);
        ChatRoom chatRoom = getChatRoomAndValidateParticipant(chatRoomId, member.getId());

        if (chatRoom.getStatus() == ChatRoomStatus.DELETED) {
            throw new ChatException(ChatErrorCode.CHAT_ROOM_ALREADY_DELETED);
        }

        chatRoom.leave();
    }

    // 채팅방 목록 응답 조립 - 마지막 메시지, 상대방 정보, 상품 썸네일 배치 조회
    private ChatRoomListResDto buildChatRoomList(List<ChatRoom> chatRooms, Long currentMemberId) {
        if (chatRooms.isEmpty()) {
            return new ChatRoomListResDto(List.of());
        }

        List<Long> lastMessageIds = chatRooms.stream()
                .map(ChatRoom::getLastMessageId)
                .filter(id -> id != null)
                .toList();

        Map<Long, ChatMessage> lastMessageMap = chatMessageRepository.findAllByIdIn(lastMessageIds)
                .stream()
                .collect(Collectors.toMap(ChatMessage::getId, Function.identity()));

        List<Long> allMemberIds = chatRooms.stream()
                .flatMap(room -> Stream.of(room.getSellerId(), room.getBuyerId()))
                .distinct()
                .toList();

        Map<Long, Member> memberMap = memberRepository.findAllById(allMemberIds)
                .stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));

        List<Long> productIds = chatRooms.stream()
                .map(ChatRoom::getProductId)
                .distinct()
                .toList();

        Map<Long, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        Map<Long, String> thumbnailMap = productImageRepository
                .findByProduct_IdInOrderByProduct_IdAscDisplayOrderAsc(productIds)
                .stream()
                .collect(Collectors.toMap(
                        img -> img.getProduct().getId(),
                        ProductImage::getImageUrl,
                        (first, second) -> first
                ));

        List<ChatRoomSummaryResDto> summaries = chatRooms.stream()
                .map(room -> chatMapper.toChatRoomSummaryResDto(
                        room,
                        currentMemberId,
                        memberMap.get(room.getSellerId()),
                        memberMap.get(room.getBuyerId()),
                        productMap.get(room.getProductId()),
                        thumbnailMap.get(room.getProductId()),
                        lastMessageMap.get(room.getLastMessageId())
                ))
                .toList();

        return new ChatRoomListResDto(summaries);
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
