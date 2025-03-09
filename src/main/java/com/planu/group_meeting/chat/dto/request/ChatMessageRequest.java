package com.planu.group_meeting.chat.dto.request;

import lombok.Data;

@Data
public class ChatMessageRequest {

    private Long messageId;
    private Integer type;
    private String message;

}
