package com.planu.group_meeting.chat.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageStatus {

    private Long id;
    private Long messageId;
    private Long userId;
    private Boolean isRead;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
