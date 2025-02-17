package com.planu.group_meeting.dto;

import com.planu.group_meeting.entity.GroupScheduleComment;
import lombok.Data;

public class GroupScheduleCommentDTO {

    @Data
    public static class GroupScheduleCommentRequest {
        private String message;

        public GroupScheduleComment toEntity(Long userId, Long groupId, Long groupScheduleId) {
            return GroupScheduleComment.builder()
                    .userId(userId)
                    .groupId(groupId)
                    .groupScheduleId(groupScheduleId)
                    .message(message)
                    .build();
        }
    }
}
