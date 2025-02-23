package com.planu.group_meeting.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupScheduleComment {
    private Long id;
    private Long userId;
    private Long groupId;
    private Long groupScheduleId;
    private String message;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
