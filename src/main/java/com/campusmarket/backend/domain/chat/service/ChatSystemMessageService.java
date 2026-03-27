package com.campusmarket.backend.domain.chat.service;

import com.campusmarket.backend.domain.chat.constant.MessageType;
import com.campusmarket.backend.domain.chat.dto.response.ChatMessageResDto;
import com.campusmarket.backend.domain.chat.entity.ChatMessage;
import com.campusmarket.backend.domain.chat.entity.ChatRoom;
import com.campusmarket.backend.domain.chat.repository.ChatMessageRepository;
import com.campusmarket.backend.domain.chat.repository.ChatRoomRepository;
import com.campusmarket.backend.domain.member.entity.Member;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatSystemMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    // 회원 탈퇴 시 해당 회원이 참여한 모든 채팅방에 시스템 메시지 전송
    public void sendWithdrawnUserMessage(Long memberId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByMemberId(memberId);
        for (ChatRoom chatRoom : chatRooms) {
            ChatMessage message = save(chatRoom, null, MessageType.SYSTEM, "탈퇴한 사용자입니다. 더 이상 채팅할 수 없습니다.", null);
            chatRoom.updateLastMessage(message.getId(), message.getCreatedAt());
            broadcast(chatRoom.getId(), ChatMessageResDto.of(message, null));
        }
    }

    // 판매 완료 시 해당 상품의 모든 채팅방에 시스템 메시지 전송
    public void sendProductSoldMessage(Long productId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByProductId(productId);
        for (ChatRoom chatRoom : chatRooms) {
            ChatMessage message = save(chatRoom, null, MessageType.SYSTEM, "판매 완료된 상품입니다.", null);
            chatRoom.updateLastMessage(message.getId(), message.getCreatedAt());
            broadcast(chatRoom.getId(), ChatMessageResDto.of(message, null));
        }
    }

    // LOCKER 거래 수락 시 양측 사물함 공유
    public void sendLockerShareMessages(ChatRoom chatRoom, Member seller, Member buyer) {
        String topic = topicOf(chatRoom.getId());

        ChatMessage sellerMsg = save(chatRoom, chatRoom.getSellerId(), MessageType.LOCKER_SHARE,
                seller != null ? seller.getLockerName() : null, null);
        broadcast(topic, ChatMessageResDto.of(sellerMsg, seller != null ? seller.getNickname() : null));

        ChatMessage buyerMsg = save(chatRoom, chatRoom.getBuyerId(), MessageType.LOCKER_SHARE,
                buyer != null ? buyer.getLockerName() : null, null);
        broadcast(topic, ChatMessageResDto.of(buyerMsg, buyer != null ? buyer.getNickname() : null));

        chatRoom.updateLastMessage(buyerMsg.getId(), buyerMsg.getCreatedAt());
    }

    // FACE_TO_FACE 거래 수락 시 시간표 공유 + 빈 시간 계산 시스템 메시지
    public void sendTimetableShareMessages(ChatRoom chatRoom, Member seller, Member buyer) {
        String topic = topicOf(chatRoom.getId());

        ChatMessage sellerMsg = save(chatRoom, chatRoom.getSellerId(), MessageType.TIMETABLE_SHARE,
                seller != null ? seller.getTimetableImageUrl() : null, null);
        broadcast(topic, ChatMessageResDto.of(sellerMsg, seller != null ? seller.getNickname() : null));

        ChatMessage buyerMsg = save(chatRoom, chatRoom.getBuyerId(), MessageType.TIMETABLE_SHARE,
                buyer != null ? buyer.getTimetableImageUrl() : null, null);
        broadcast(topic, ChatMessageResDto.of(buyerMsg, buyer != null ? buyer.getNickname() : null));

        chatRoom.updateLastMessage(buyerMsg.getId(), buyerMsg.getCreatedAt());

        String sellerData = seller != null ? seller.getTimetableData() : null;
        String buyerData = buyer != null ? buyer.getTimetableData() : null;

        // 한 명이라도 시간표 미등록이면 매칭 불가 메시지
        if (sellerData == null || buyerData == null) {
            sendNoMatchMessage(chatRoom);
            return;
        }

        sendFreeSlotMessage(chatRoom, sellerData, buyerData);
    }

    private void sendFreeSlotMessage(ChatRoom chatRoom, String sellerData, String buyerData) {
        try {
            List<TimetableEntry> sellerEntries = parseEntries(sellerData);
            List<TimetableEntry> buyerEntries = parseEntries(buyerData);

            List<FreeSlot> freeSlots = computeFreeSlots(sellerEntries, buyerEntries);

            if (freeSlots.isEmpty()) {
                sendNoMatchMessage(chatRoom);
                return;
            }

            Map<String, List<FreeSlot>> payload = Map.of("freeSlots", freeSlots);
            String metadataJson = objectMapper.writeValueAsString(payload);

            ChatMessage sysMsg = save(chatRoom, null, MessageType.SYSTEM, "서로 비어있는 시간대", metadataJson);
            chatRoom.updateLastMessage(sysMsg.getId(), sysMsg.getCreatedAt());
            broadcast(topicOf(chatRoom.getId()), ChatMessageResDto.of(sysMsg, null));

        } catch (Exception e) {
            log.warn("시간표 빈 시간 계산 실패 - chatRoomId={}", chatRoom.getId(), e);
            sendNoMatchMessage(chatRoom);
        }
    }

    private void sendNoMatchMessage(ChatRoom chatRoom) {
        ChatMessage sysMsg = save(chatRoom, null, MessageType.SYSTEM, "매칭이 되는 시간이 없습니다!", null);
        chatRoom.updateLastMessage(sysMsg.getId(), sysMsg.getCreatedAt());
        broadcast(topicOf(chatRoom.getId()), ChatMessageResDto.of(sysMsg, null));
    }

    // --- 시간표 파싱 및 빈 시간 계산 ---

    private List<TimetableEntry> parseEntries(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json);
        JsonNode classes = root.path("classes");
        List<TimetableEntry> result = new ArrayList<>();
        for (JsonNode cls : classes) {
            String day = cls.path("day").asText(null);
            String startTime = cls.path("start_time").asText(null);
            String endTime = cls.path("end_time").asText(null);
            if (day != null && startTime != null && endTime != null) {
                result.add(new TimetableEntry(day, startTime, endTime));
            }
        }
        return result;
    }

    private static final int DAY_START = 9 * 60;   // 09:00
    private static final int DAY_END   = 24 * 60;  // 24:00

    private List<FreeSlot> computeFreeSlots(List<TimetableEntry> entries1, List<TimetableEntry> entries2) {
        String[] days = {"월", "화", "수", "목", "금", "토", "일"};
        List<FreeSlot> result = new ArrayList<>();

        for (String day : days) {
            List<int[]> busyIntervals = Stream.concat(
                    entries1.stream().filter(e -> e.day().equals(day)),
                    entries2.stream().filter(e -> e.day().equals(day))
            )
            .map(e -> new int[]{toMinutes(e.startTime()), toMinutes(e.endTime())})
            .collect(Collectors.toList());

            List<int[]> free;
            if (busyIntervals.isEmpty()) {
                free = List.of(new int[]{DAY_START, DAY_END});
            } else {
                List<int[]> merged = mergeIntervals(busyIntervals);
                free = invertIntervals(merged, DAY_START, DAY_END);
            }

            for (int[] slot : free) {
                result.add(new FreeSlot(day, toTime(slot[0]), toTime(slot[1])));
            }
        }

        return result;
    }

    private List<int[]> mergeIntervals(List<int[]> intervals) {
        intervals.sort(Comparator.comparingInt(a -> a[0]));
        List<int[]> merged = new ArrayList<>();
        int[] current = intervals.get(0).clone();
        for (int i = 1; i < intervals.size(); i++) {
            int[] next = intervals.get(i);
            if (next[0] <= current[1]) {
                current[1] = Math.max(current[1], next[1]);
            } else {
                merged.add(current);
                current = next.clone();
            }
        }
        merged.add(current);
        return merged;
    }

    private List<int[]> invertIntervals(List<int[]> busy, int start, int end) {
        List<int[]> free = new ArrayList<>();
        int cursor = start;
        for (int[] interval : busy) {
            if (cursor < interval[0]) {
                free.add(new int[]{cursor, interval[0]});
            }
            cursor = Math.max(cursor, interval[1]);
        }
        if (cursor < end) {
            free.add(new int[]{cursor, end});
        }
        return free;
    }

    private int toMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    private String toTime(int minutes) {
        return String.format("%02d:%02d", minutes / 60, minutes % 60);
    }

    // --- 공통 유틸 ---

    private ChatMessage save(ChatRoom chatRoom, Long senderId, MessageType type, String content, String metadata) {
        return chatMessageRepository.save(ChatMessage.builder()
                .chatRoom(chatRoom)
                .senderId(senderId)
                .messageType(type)
                .content(content)
                .metadata(metadata)
                .build());
    }

    private void broadcast(Long chatRoomId, ChatMessageResDto dto) {
        broadcast(topicOf(chatRoomId), dto);
    }

    private void broadcast(String topic, ChatMessageResDto dto) {
        messagingTemplate.convertAndSend(topic, dto);
    }

    private String topicOf(Long chatRoomId) {
        return "/sub/chat/" + chatRoomId;
    }

    // --- 내부 record ---

    private record TimetableEntry(String day, String startTime, String endTime) {}

    private record FreeSlot(String day, String start_time, String end_time) {}
}
