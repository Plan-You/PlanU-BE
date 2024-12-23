package com.planu.group_meeting.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupScheduleParticipant {
    private Long userId;
    private Long groupId;
    private Long groupScheduleId;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public GroupScheduleParticipant(Long groupScheduleId, Long userId, Long groupId) {
        this.groupScheduleId = groupScheduleId;
        this.userId = userId;
        this.groupId = groupId;
    }
}
