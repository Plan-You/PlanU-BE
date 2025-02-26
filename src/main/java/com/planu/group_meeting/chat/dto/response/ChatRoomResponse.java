package com.planu.group_meeting.chat.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatRoomResponse {

    private Long groupId;
    private String groupName;
    private String groupImageUrl;
    private Long participant;
    private Boolean isPin;
    private String lastChat;
    private String lastChatDate;
    private String lastChatTime;
    private Integer unreadChats;
}
