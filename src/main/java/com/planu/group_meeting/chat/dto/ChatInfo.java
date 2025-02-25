package com.planu.group_meeting.chat.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatInfo {
    private String lastChat;
    private String lastChatDate;
    private String lastChatTime;
    private Long unreadChats;
}
