package com.planu.group_meeting.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.planu.group_meeting.entity.common.EventType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private String relatedUrl;
    private boolean isRead;

    private LocalDateTime createdDate;

    protected NotificationDTO(EventType eventType, Long senderId, Long receiverId, String contents, String relatedUrl) {
        this.eventType = eventType;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.contents = contents;
        this.relatedUrl = relatedUrl;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class NotificationListResponse{
        List<NotificationDTO> notificationList = new ArrayList<>();
    }

    @Getter
    public static class UnreadNotificationResponse{
        private boolean isExistUnReadNotification;
    }

    protected static String formatUrl(NotificationUrl url, Object... params) {
        return String.format(url.getPattern(), params);
    }

    @Getter
    public enum NotificationUrl {
        SCHEDULE_DETAIL("/mySchedule/%s"),
        GROUP_SCHEDULE_DETAIL("/group/%s/calendar/schedule/%s"),
        FRIEND_MANAGEMENT("/myPage/friendsManagement"),
        GROUP_LIST("/groupList"),
        GROUP_MEMBER_LIST("/group/%s/members"),
        GROUP_CALENDAR("/group/%s/groupCalendar");

        private final String pattern;

        NotificationUrl(String pattern) {
            this.pattern = pattern;
        }

    }

    public static class ScheduleNotification extends NotificationDTO {
        public ScheduleNotification(Long receiverId, String contents, Long scheduleId) {
            super(EventType.SCHEDULE_REMINDER, SYSTEM_SENDER_ID, receiverId, contents,
                    formatUrl(NotificationUrl.SCHEDULE_DETAIL, scheduleId));
        }
    }

    public static class GroupScheduleNotification extends NotificationDTO {
        public GroupScheduleNotification(Long receiverId, String contents, Long groupId, Long scheduleId) {
            super(EventType.SCHEDULE_REMINDER, SYSTEM_SENDER_ID, receiverId, contents,
                    formatUrl(NotificationUrl.GROUP_SCHEDULE_DETAIL, groupId, scheduleId));
        }
    }

    public static class FriendNotification extends NotificationDTO {
        public FriendNotification(EventType eventType, Long senderId, Long receiverId, String contents) {
            super(eventType, senderId, receiverId, contents, NotificationUrl.FRIEND_MANAGEMENT.getPattern());
        }
    }

    public static class GroupDeleteNotification extends NotificationDTO {
        public GroupDeleteNotification(Long receiverId, String contents) {
            super(EventType.GROUP_DELETE, SYSTEM_SENDER_ID, receiverId, contents,
                    NotificationUrl.GROUP_LIST.getPattern());
        }
    }

    public static class GroupInviteNotification extends NotificationDTO {
        public GroupInviteNotification(Long senderId, Long receiverId, String contents) {
            super(EventType.GROUP_INVITE, senderId, receiverId, contents,
                    NotificationUrl.GROUP_LIST.getPattern());
        }
    }

    public static class GroupAcceptNotification extends NotificationDTO {
        public GroupAcceptNotification(Long senderId, Long receiverId, String contents, Long groupId) {
            super(EventType.GROUP_ACCEPT, senderId, receiverId, contents,
                    formatUrl(NotificationUrl.GROUP_MEMBER_LIST, groupId));
        }
    }

    public static class GroupExpelNotification extends NotificationDTO {
        public GroupExpelNotification(Long senderId, Long receiverId, String contents) {
            super(EventType.GROUP_EXPEL, senderId, receiverId, contents,
                    NotificationUrl.GROUP_LIST.getPattern());
        }
    }

    public static class GroupScheduleDeleteNotification extends NotificationDTO {
        public GroupScheduleDeleteNotification(Long receiverId, String contents, Long groupId) {
            super(EventType.GROUP_SCHEDULE_DELETE, SYSTEM_SENDER_ID, receiverId, contents,
                    formatUrl(NotificationUrl.GROUP_CALENDAR, groupId));
        }
    }

    public static class GroupScheduleCreateNotification extends NotificationDTO {
        public GroupScheduleCreateNotification(Long receiverId, String contents, Long groupId, Long scheduleId) {
            super(EventType.GROUP_SCHEDULE_CREATE, SYSTEM_SENDER_ID, receiverId, contents,
                    formatUrl(NotificationUrl.GROUP_SCHEDULE_DETAIL, groupId, scheduleId));
        }
    }

    public static class GroupScheduleCommentNotification extends NotificationDTO {
        public GroupScheduleCommentNotification(Long senderId, Long receiverId, String contents, Long groupId, Long scheduleId) {
            super(EventType.COMMENT, senderId, receiverId, contents,
                    formatUrl(NotificationUrl.GROUP_SCHEDULE_DETAIL, groupId, scheduleId));
        }
    }
}