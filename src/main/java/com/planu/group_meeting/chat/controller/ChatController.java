package com.planu.group_meeting.chat.controller;


import com.planu.group_meeting.chat.dto.ChatMessage;
import com.planu.group_meeting.chat.dto.request.ChatMessageRequest;
import com.planu.group_meeting.chat.dto.response.ChatMessageResponse;
import com.planu.group_meeting.chat.dto.response.ChatRoomResponse;
import com.planu.group_meeting.chat.handler.ReadMessageBatchProcessor;
import com.planu.group_meeting.chat.service.ChatService;
import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.DataResponse;
import jakarta.validation.constraints.Max;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations simpMessageSendingOperations;
    private final ChatService chatService;
    private final ReadMessageBatchProcessor batchProcessor;

    @Transactional
    @MessageMapping("/chat/group/{groupId}")
    public void chat(ChatMessageRequest message, @DestinationVariable("groupId") Long groupId, StompHeaderAccessor accessor){
        String username = (String) accessor.getSessionAttributes().get("username");

        ChatMessage chatMessage = chatService.save(groupId, username, message.getType(), message.getMessage());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = chatMessage.getCreatedDate().format(dateFormatter);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String time = chatMessage.getCreatedDate().format(timeFormatter);

        chatService.updateReadStatus(chatMessage.getId(), username);

        ChatMessageResponse chatMessageResponse = ChatMessageResponse.builder()
                                                    .messageId(chatMessage.getId())
                                                    .type(message.getType())
                                                    .sender(username)
                                                    .message(message.getMessage())
                                                    .unReadCount(chatService.getUnreadCount(chatMessage.getId()))
                                                    .ChatDate(date)
                                                    .ChatTime(time)
                                                    .build();

        simpMessageSendingOperations.convertAndSend("/sub/chat/group/" + groupId, chatMessageResponse);
    }

    @Transactional
    @MessageMapping("/chat/read/{messageId}/{groupId}")
    public void readChat(@DestinationVariable("messageId") Long messageId, @DestinationVariable("groupId") Long groupId, StompHeaderAccessor accessor) {
        String username = (String) accessor.getSessionAttributes().get("username");
        chatService.updateReadStatus(messageId, username);
        batchProcessor.addReadRequest(messageId, groupId);
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

    @ResponseBody
    @GetMapping("/chats/messages")
    public ResponseEntity<DataResponse<List<ChatMessageResponse>>> getChats(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("groupId") Long groupId,
            @RequestParam(value = "messageId", required = false) Long messageId) {

        List<ChatMessageResponse> chatMessageResponseList = chatService.getMessages(userDetails.getId(), groupId, messageId);
        DataResponse<List<ChatMessageResponse>> response = new DataResponse<>(chatMessageResponseList);
        return ResponseEntity.ok(response);
    }

    @ResponseBody
    @GetMapping("/chats/messages/update")
    public ResponseEntity<DataResponse<List<ChatMessageResponse>>> getUpdateChats(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("groupId") Long groupId,
            @RequestParam("startId") Long startId,
            @RequestParam("endId") Long endId
    ) {
        List<ChatMessageResponse> chatMessageResponseList = chatService.getUpdateMessages(userDetails.getId(), groupId, startId, endId);
        DataResponse<List<ChatMessageResponse>> response = new DataResponse<>(chatMessageResponseList);
        return ResponseEntity.ok(response);
    }
}
