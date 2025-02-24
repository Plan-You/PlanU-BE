package com.planu.group_meeting.chat.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessage {

    private Long id;
    private Long userId;
    private Long groupId;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
