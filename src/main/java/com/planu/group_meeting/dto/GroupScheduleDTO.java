package com.planu.group_meeting.dto;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

public class GroupScheduleDTO {
    @Data
    public static class GroupScheduleRequest {

    }

    @Getter
    public static class todayScheduleResponse {
        private Long id;
        private String title;
        private LocalDateTime startDateTime;
        private String location;
    }

    @Getter
    public static class scheduleOverViewResponse {
        private Long id;
        private String title;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private String color;
    }
}
