package com.planu.group_meeting.dto;

import com.planu.group_meeting.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NotificationDTO {
    private Long id;
    private User receiver;
    private User provider;
    private Object content;
    private String relatedUrl;
    private boolean isRead;
}
