package com.campusmarket.backend.domain.chat.controller;

import com.campusmarket.backend.domain.chat.dto.request.SendMessageReqDto;
import com.campusmarket.backend.domain.chat.dto.response.ChatMessageResDto;
import com.campusmarket.backend.domain.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat/{chatRoomId}")
    @SendTo("/sub/chat/{chatRoomId}")
    public ChatMessageResDto sendMessage(
            @DestinationVariable Long chatRoomId,
            SendMessageReqDto reqDto
    ) {
        return chatMessageService.sendMessage(chatRoomId, reqDto);
    }
}
