package com.planu.group_meeting.chat.dto;

import lombok.Data;

@Data
public class ChatMessageRequest {

    private Long groupId;
    private String sender;
    private String message;

}
