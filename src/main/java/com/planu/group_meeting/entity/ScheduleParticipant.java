package com.planu.group_meeting.entity;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ScheduleParticipant {
    private Long scheduleId;
    private Long userId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public ScheduleParticipant(Long scheduleId, Long userId){
        this.scheduleId = scheduleId;
        this.userId = userId;
    }
}
