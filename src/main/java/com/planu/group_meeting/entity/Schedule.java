package com.planu.group_meeting.entity;
import com.planu.group_meeting.entity.common.ScheduleVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class Schedule {
    private Long id;
    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String color;
    private String memo;
    private String location;
    private String latitude;
    private String longitude;
    ScheduleVisibility visibility;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;

}
