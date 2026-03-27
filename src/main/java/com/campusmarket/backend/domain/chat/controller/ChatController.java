package com.campusmarket.backend.domain.chat.controller;

import com.campusmarket.backend.domain.chat.dto.request.ChatRoomCreateReqDto;
import com.campusmarket.backend.domain.chat.dto.request.ProposalRespondReqDto;
import com.campusmarket.backend.domain.chat.dto.request.TradeProposalReqDto;
import com.campusmarket.backend.domain.chat.dto.response.*;
import com.campusmarket.backend.domain.chat.service.BlockService;
import com.campusmarket.backend.domain.chat.service.ChatMessageService;
import com.campusmarket.backend.domain.chat.service.ChatRoomService;
import com.campusmarket.backend.domain.chat.service.TradeProposalService;
import com.campusmarket.backend.global.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/rooms")
public class ChatController implements ChatControllerDocs {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final TradeProposalService tradeProposalService;
    private final BlockService blockService;

    @Override
    @PostMapping
    public ApiResponse<ChatRoomEnterResDto> enterChatRoom(
            @RequestHeader("guestUuid") String guestUuid,
            @Valid @RequestBody ChatRoomCreateReqDto reqDto
    ) {
        return ApiResponse.success(chatRoomService.enterChatRoom(guestUuid, reqDto));
    }

    @Override
    @GetMapping("/selling")
    public ApiResponse<ChatRoomListResDto> getSellingChatRoomList(
            @RequestHeader("guestUuid") String guestUuid
    ) {
        return ApiResponse.success(chatRoomService.getSellingChatRoomList(guestUuid));
    }

    @Override
    @GetMapping("/buying")
    public ApiResponse<ChatRoomListResDto> getBuyingChatRoomList(
            @RequestHeader("guestUuid") String guestUuid
    ) {
        return ApiResponse.success(chatRoomService.getBuyingChatRoomList(guestUuid));
    }

    @Override
    @GetMapping("/{chatRoomId}/messages")
    public ApiResponse<ChatMessageListResDto> getMessageList(
            @RequestHeader("guestUuid") String guestUuid,
            @PathVariable Long chatRoomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.success(chatMessageService.getMessageList(guestUuid, chatRoomId, page, size));
    }

    @Override
    @PostMapping("/{chatRoomId}/proposals")
    public ApiResponse<TradeProposalResDto> sendTradeProposal(
            @RequestHeader("guestUuid") String guestUuid,
            @PathVariable Long chatRoomId,
            @Valid @RequestBody TradeProposalReqDto reqDto
    ) {
        return ApiResponse.success(tradeProposalService.sendTradeProposal(guestUuid, chatRoomId, reqDto));
    }

    @Override
    @PatchMapping("/{chatRoomId}/proposals/{proposalId}")
    public ApiResponse<TradeProposalResDto> respondToProposal(
            @RequestHeader("guestUuid") String guestUuid,
            @PathVariable Long chatRoomId,
            @PathVariable Long proposalId,
            @Valid @RequestBody ProposalRespondReqDto reqDto
    ) {
        return ApiResponse.success(tradeProposalService.respondToProposal(guestUuid, chatRoomId, proposalId, reqDto));
    }

    @Override
    @DeleteMapping("/{chatRoomId}")
    public ApiResponse<Void> leaveChatRoom(
            @RequestHeader("guestUuid") String guestUuid,
            @PathVariable Long chatRoomId
    ) {
        chatRoomService.leaveChatRoom(guestUuid, chatRoomId);
        return ApiResponse.success(null);
    }

    @Override
    @PostMapping("/{chatRoomId}/block")
    public ApiResponse<Void> blockUser(
            @RequestHeader("guestUuid") String guestUuid,
            @PathVariable Long chatRoomId
    ) {
        blockService.blockUser(guestUuid, chatRoomId);
        return ApiResponse.success(null);
    }
}
