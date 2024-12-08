package com.planu.group_meeting.entity;

import com.planu.group_meeting.entity.common.ScheduleVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupSchedule {
    private Long id;
    private Long groupId;
    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String color;
    ScheduleVisibility visibility;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
