package com.planu.group_meeting.chat.controller;


import com.planu.group_meeting.chat.dto.ChatMessageRequest;
import com.planu.group_meeting.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations simpMessageSendingOperations;
    private final ChatService chatService;
    @Transactional
    @MessageMapping("/chat/group/{groupId}")
    public void chat(ChatMessageRequest message, @DestinationVariable("groupId") Long groupId, StompHeaderAccessor accessor){
        String username = (String) accessor.getSessionAttributes().get("username");
        message.setSender(username);
        chatService.save(groupId, username, message.getMessage());
        simpMessageSendingOperations.convertAndSend("/sub/chat/group/" + groupId, message);
    }

}
