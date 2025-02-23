package com.planu.group_meeting.chat.dto;

import lombok.Data;

@Data
public class ChatMessageRequest {

    private Long type;
    private String sender;
    private String message;

}
