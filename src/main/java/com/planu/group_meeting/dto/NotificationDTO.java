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
    public static final long SYSTEM_SENDER_ID = -1L;

    private Long id;

    @JsonIgnore
    private Long senderId;
    @JsonIgnore
    private Long receiverId;

    private EventType eventType;
    private String contents;
    private boolean isRead;

    @Getter
    @Setter
    public static class FriendNotification {
        private EventType eventType;
        private Long fromUserId;
        private Long toUserId;
        private String contents;

        public FriendNotification(EventType eventType, Long fromUserId, Long toUserId, String contents) {
            this.eventType = eventType;
            this.fromUserId = fromUserId;
            this.toUserId = toUserId;
            this.contents = contents;

        }
    }

    @Getter
    @Setter
    public static class ScheduleNotification{
        private EventType eventType;
        private Long senderId;
        private Long receiverId;
        private String contents;

        public ScheduleNotification(Long receiverId, String contents){
            this.eventType =EventType.SCHEDULE_REMINDER;
            this.senderId = SYSTEM_SENDER_ID;        // 서버가 보내는 알림
            this.receiverId = receiverId;
            this.contents = contents;
        }
    }

    @Getter
    @Setter
    public static class GroupDeleteNotification{
        private EventType eventType;
        private Long senderId;
        private Long receiverId;
        private String contents;

        public GroupDeleteNotification(Long receiverId, String contents){
            this.eventType =EventType.GROUP_DELETE;
            this.senderId = SYSTEM_SENDER_ID;
            this.receiverId = receiverId;
            this.contents = contents;
        }
    }



}
