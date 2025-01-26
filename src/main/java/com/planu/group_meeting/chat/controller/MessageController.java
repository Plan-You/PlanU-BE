package com.planu.group_meeting.chat.controller;


import com.planu.group_meeting.chat.dto.ChatMessageRequest;
import com.planu.group_meeting.config.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    @MessageMapping("/chats")
    public void mesasge(ChatMessageRequest message, @AuthenticationPrincipal CustomUserDetails userDetails){

    }
}
