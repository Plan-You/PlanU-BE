package com.planu.group_meeting.chat.controller;


import com.planu.group_meeting.chat.dto.request.ChatMessageRequest;
import com.planu.group_meeting.chat.dto.response.ChatRoomResponse;
import com.planu.group_meeting.chat.service.ChatService;
import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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

    @Transactional
    @MessageMapping("/chat/read/{messageId}")
    public void readChat(@DestinationVariable("groupId") Long groupId, @DestinationVariable("messageId") Long messageId, StompHeaderAccessor accessor) {
        String username = (String) accessor.getSessionAttributes().get("username");
    }

    @ResponseBody
    @GetMapping("/chats")
    public ResponseEntity<DataResponse<List<ChatRoomResponse>>> chatRooms(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ChatRoomResponse> chatRooms = chatService.getChatRooms(userDetails.getId());
        DataResponse<List<ChatRoomResponse>> response = new DataResponse<>(chatRooms);
        return ResponseEntity.ok(response);
    }

    @ResponseBody
    @GetMapping("/chats/search/{name}")
    public ResponseEntity<DataResponse<List<ChatRoomResponse>>> serachChatRooms(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("name") String name) {
        List<ChatRoomResponse> chatRooms = chatService.searchChatRooms(userDetails.getId(), name);
        DataResponse<List<ChatRoomResponse>> response = new DataResponse<>(chatRooms);
        return ResponseEntity.ok(response);
    }
}
