package com.planu.group_meeting.chat.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessageResponse {

    private Long messageId;
    private Long type;
    private String message;
    private String sender;
}