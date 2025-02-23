package com.planu.group_meeting.chat.controller;


import com.planu.group_meeting.chat.dto.ChatMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    @MessageMapping("/chat/group/{groupId}")
    public void chat(ChatMessageRequest message, @DestinationVariable("groupId") Long groupId, Principal principal){
        message.setSender(principal.getName());
        simpMessageSendingOperations.convertAndSend("/sub/chat/group/" + groupId, message);
    }

}
