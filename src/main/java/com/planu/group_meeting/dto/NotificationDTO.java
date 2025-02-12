package com.planu.group_meeting.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.planu.group_meeting.entity.common.EventType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NotificationDTO {
    private Long id;

    @JsonIgnore
    private Long senderId;
    @JsonIgnore
    private Long receiverId;

    private EventType eventType;
    private String contents;
    private boolean isRead;
}
