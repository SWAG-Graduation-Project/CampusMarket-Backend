package com.campusmarket.backend.domain.chat.controller;

import com.campusmarket.backend.domain.chat.dto.request.ChatRoomCreateReqDto;
import com.campusmarket.backend.domain.chat.dto.request.ProposalRespondReqDto;
import com.campusmarket.backend.domain.chat.dto.request.TradeProposalReqDto;
import com.campusmarket.backend.domain.chat.dto.response.*;
import com.campusmarket.backend.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Chat", description = "채팅 API")
public interface ChatControllerDocs {

    @Operation(summary = "채팅방 입장/생성", description = "상품에 대한 채팅방 입장. 없으면 신규 생성.")
    ApiResponse<ChatRoomEnterResDto> enterChatRoom(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid,
            @Valid @RequestBody ChatRoomCreateReqDto reqDto
    );

    @Operation(summary = "판매 채팅 목록 조회", description = "내가 판매자인 채팅방 목록. 나간 방 제외, 최신 메시지 순.")
    ApiResponse<ChatRoomListResDto> getSellingChatRoomList(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid
    );

    @Operation(summary = "구매 채팅 목록 조회", description = "내가 구매자인 채팅방 목록. 나간 방 제외, 최신 메시지 순.")
    ApiResponse<ChatRoomListResDto> getBuyingChatRoomList(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid
    );

    @Operation(summary = "메시지 내역 조회", description = "채팅방 메시지 내역을 페이지 단위로 조회.")
    ApiResponse<ChatMessageListResDto> getMessageList(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid,
            @PathVariable Long chatRoomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    );

    @Operation(summary = "거래 제안", description = "LOCKER(사물함) 또는 FACE_TO_FACE(대면) 거래 제안.")
    ApiResponse<TradeProposalResDto> sendTradeProposal(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid,
            @PathVariable Long chatRoomId,
            @Valid @RequestBody TradeProposalReqDto reqDto
    );

    @Operation(summary = "거래 제안 수락/거절", description = "accept=true 면 수락, false 면 거절.")
    ApiResponse<TradeProposalResDto> respondToProposal(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid,
            @PathVariable Long chatRoomId,
            @PathVariable Long proposalId,
            @Valid @RequestBody ProposalRespondReqDto reqDto
    );

    @Operation(summary = "채팅방 나가기", description = "채팅방 상태를 DELETED로 변경.")
    ApiResponse<Void> leaveChatRoom(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid,
            @PathVariable Long chatRoomId
    );

    @Operation(summary = "채팅 상대 차단", description = "채팅방 기준으로 상대방을 차단. 이후 상대방의 채팅방 입장 불가.")
    ApiResponse<Void> blockUser(
            @Parameter(hidden = true) @RequestHeader("guestUuid") String guestUuid,
            @PathVariable Long chatRoomId
    );
}
