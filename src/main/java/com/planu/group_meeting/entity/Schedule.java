package com.planu.group_meeting.entity;
import com.planu.group_meeting.dto.ScheduleDto;
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
    private Long userId;
    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String color;
    ScheduleVisibility visibility;
    private String memo;
    private String location;
    private String latitude;
    private String longitude;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public void updateSchedule(ScheduleDto.ScheduleSaveRequest request){
        this.title = request.getTitle();
        this.startDateTime = request.getStartDateTime();
        this.endDateTime = request.getEndDateTime();
        this.color = request.getColor();
        this.memo = request.getMemo();
        this.location = request.getLocation();
        this.latitude = request.getLatitude();
        this.longitude = request.getLongitude();
    }

}
