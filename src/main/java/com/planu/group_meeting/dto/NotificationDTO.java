package com.planu.group_meeting.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.planu.group_meeting.entity.common.EventType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    public static class ScheduleNotification extends NotificationDTO {
        public ScheduleNotification(Long receiverId, String contents) {
            this.setEventType(EventType.SCHEDULE_REMINDER);
            this.setSenderId(SYSTEM_SENDER_ID);
            this.setReceiverId(receiverId);
            this.setContents(contents);
        }
    }

    @Getter
    @Setter
    public static class GroupScheduleNotification extends NotificationDTO{
        public GroupScheduleNotification(Long receiverId, String contents){
            this.setEventType(EventType.SCHEDULE_REMINDER);
            this.setSenderId(SYSTEM_SENDER_ID);
            this.setReceiverId(receiverId);
            this.setContents(contents);
        }
    }

    @Getter
    @Setter
    public static class FriendNotification extends NotificationDTO {
        public FriendNotification(EventType eventType, Long senderId, Long receiverId, String contents) {
            this.setEventType(eventType);
            this.setSenderId(senderId);
            this.setReceiverId(receiverId);
            this.setContents(contents);
        }
    }

    @Getter
    @Setter
    public static class GroupDeleteNotification extends NotificationDTO {
        public GroupDeleteNotification(Long receiverId, String contents) {
            this.setEventType(EventType.GROUP_DELETE);
            this.setSenderId(SYSTEM_SENDER_ID);
            this.setReceiverId(receiverId);
            this.setContents(contents);
        }
    }

    @Getter
    @Setter
    public static class GroupInviteNotification extends NotificationDTO {
        public GroupInviteNotification(Long senderId, Long receiverId, String contents) {
            this.setEventType(EventType.GROUP_INVITE);
            this.setSenderId(senderId);
            this.setReceiverId(receiverId);
            this.setContents(contents);
        }
    }

    @Getter
    @Setter
    public static class GroupAcceptNotification extends NotificationDTO {
        public GroupAcceptNotification(Long senderId, Long receiverId, String contents) {
            this.setEventType(EventType.GROUP_ACCEPT);
            this.setSenderId(senderId);
            this.setReceiverId(receiverId);
            this.setContents(contents);
        }
    }

    @Getter
    @Setter
    public static class GroupExpelNotification extends NotificationDTO {
        public GroupExpelNotification(Long senderId, Long receiverId, String contents) {
            this.setEventType(EventType.GROUP_EXPEL);
            this.setSenderId(senderId);
            this.setReceiverId(receiverId);
            this.setContents(contents);
        }
    }

    @Getter
    @Setter
    public static class GroupScheduleDeleteNotification extends NotificationDTO {
        public GroupScheduleDeleteNotification(Long receiverId, String contents) {
            this.setEventType(EventType.GROUP_SCHEDULE_DELETE);
            this.setSenderId(SYSTEM_SENDER_ID);
            this.setReceiverId(receiverId);
            this.setContents(contents);
        }
    }

    public static class GroupScheduleCreateNotification extends NotificationDTO{
        public GroupScheduleCreateNotification(Long receiverId, String contents){
            this.setEventType(EventType.GROUP_SCHEDULE_CREATE);
            this.setSenderId(SYSTEM_SENDER_ID);
            this.setReceiverId(receiverId);
            this.setContents(contents);
        }
    }

    public static class GroupScheduleCommentNotification extends NotificationDTO{
        public GroupScheduleCommentNotification(Long senderId, Long receiverId, String contents){
            this.setEventType(EventType.COMMENT);
            this.setSenderId(senderId);
            this.setReceiverId(receiverId);
            this.setContents(contents);
        }
    }
}