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
        GROUP_CALENDAR("/group/%s/groupCalendar"),
        MY_CALENDAR("/myCalendar");

        private final String pattern;

        NotificationUrl(String pattern) {
            this.pattern = pattern;
        }

    }

    // 일정 알림 (개인 일정에 대한 알림)
    public static class ScheduleNotification extends NotificationDTO {
        public ScheduleNotification(Long receiverId, String contents, Long scheduleId) {
            super(EventType.SCHEDULE_REMINDER, SYSTEM_SENDER_ID, receiverId, contents,
                    formatUrl(NotificationUrl.SCHEDULE_DETAIL, scheduleId));
        }
    }

    // 그룹 일정 알림 (그룹 일정에 대한 알림)
    public static class GroupScheduleNotification extends NotificationDTO {
        public GroupScheduleNotification(Long receiverId, String contents, Long groupId, Long scheduleId) {
            super(EventType.SCHEDULE_REMINDER, SYSTEM_SENDER_ID, receiverId, contents,
                    formatUrl(NotificationUrl.GROUP_SCHEDULE_DETAIL, groupId, scheduleId));
        }
    }

    // 친구 관련 알림 (친구 추가/거절 관련 알림)
    public static class FriendNotification extends NotificationDTO {
        public FriendNotification(EventType eventType, Long senderId, Long receiverId, String contents) {
            super(eventType, senderId, receiverId, contents, NotificationUrl.FRIEND_MANAGEMENT.getPattern());
        }
    }

    // 그룹 삭제 알림 (그룹 삭제에 대한 알림)
    public static class GroupDeleteNotification extends NotificationDTO {
        public GroupDeleteNotification(Long receiverId, String contents) {
            super(EventType.GROUP_DELETE, SYSTEM_SENDER_ID, receiverId, contents,
                    NotificationUrl.GROUP_LIST.getPattern());
        }
    }

    // 그룹 초대 알림 (그룹 초대에 대한 알림)
    public static class GroupInviteNotification extends NotificationDTO {
        public GroupInviteNotification(Long senderId, Long receiverId, String contents) {
            super(EventType.GROUP_INVITE, senderId, receiverId, contents,
                    NotificationUrl.GROUP_LIST.getPattern());
        }
    }

    // 그룹 초대 수락 알림
    public static class GroupAcceptNotification extends NotificationDTO {
        public GroupAcceptNotification(Long senderId, Long receiverId, String contents, Long groupId) {
            super(EventType.GROUP_ACCEPT, senderId, receiverId, contents,
                    formatUrl(NotificationUrl.GROUP_MEMBER_LIST, groupId));
        }
    }

    // 그룹 추방 알림
    public static class GroupExpelNotification extends NotificationDTO {
        public GroupExpelNotification(Long senderId, Long receiverId, String contents) {
            super(EventType.GROUP_EXPEL, senderId, receiverId, contents,
                    NotificationUrl.GROUP_LIST.getPattern());
        }
    }

    // 그룹 초대 취소 알림
    public static class GroupInviteCancelNotification extends NotificationDTO{
        public GroupInviteCancelNotification(Long senderId, Long receiverId, String contents){
            super(EventType.GROUP_INVITATION_CANCELLED,senderId, receiverId, contents, NotificationUrl.GROUP_LIST.getPattern());
        }
    }

    // 그룹원 자진 탈퇴 알림
    public static class GroupMemberLeaveNotification extends NotificationDTO{
        public GroupMemberLeaveNotification(Long senderId, Long receiverId, String contents, Long groupId){
            super(EventType.GROUP_MEMBER_LEFT, senderId, receiverId, contents,
                    formatUrl(NotificationUrl.GROUP_MEMBER_LIST,groupId));
        }
    }

    // 그룹 일정 삭제 알림
    public static class GroupScheduleDeleteNotification extends NotificationDTO {
        public GroupScheduleDeleteNotification(Long receiverId, String contents, Long groupId) {
            super(EventType.GROUP_SCHEDULE_DELETE, SYSTEM_SENDER_ID, receiverId, contents,
                    formatUrl(NotificationUrl.GROUP_CALENDAR, groupId));
        }
    }

    // 그룹 일정 생성 알림
    public static class GroupScheduleCreateNotification extends NotificationDTO {
        public GroupScheduleCreateNotification(Long receiverId, String contents, Long groupId, Long scheduleId) {
            super(EventType.GROUP_SCHEDULE_CREATE, SYSTEM_SENDER_ID, receiverId, contents,
                    formatUrl(NotificationUrl.GROUP_SCHEDULE_DETAIL, groupId, scheduleId));
        }
    }

    // 그룹 일정 댓글 알림
    public static class GroupScheduleCommentNotification extends NotificationDTO {
        public GroupScheduleCommentNotification(Long senderId, Long receiverId, String contents, Long groupId, Long scheduleId) {
            super(EventType.COMMENT, senderId, receiverId, contents,
                    formatUrl(NotificationUrl.GROUP_SCHEDULE_DETAIL, groupId, scheduleId));
        }
    }

    // 친구 생일 알림
    public static class BirthdayFriendNotification extends NotificationDTO{
        public BirthdayFriendNotification(Long receiverId, String contents){
            super(EventType.BIRTHDAY, SYSTEM_SENDER_ID ,receiverId, contents, NotificationUrl.MY_CALENDAR.getPattern());
        }
    }
}