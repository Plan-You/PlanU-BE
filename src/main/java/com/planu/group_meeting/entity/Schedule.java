package com.planu.group_meeting.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

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
    ScheduleVisibility visibility;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;

}
