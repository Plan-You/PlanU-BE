package com.planu.group_meeting.entity;

import com.planu.group_meeting.dto.GroupScheduleDTO;
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
    private String memo;
    private String location;
    private Double latitude;
    private Double longitude;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public void updateGroupSchedule(GroupScheduleDTO.GroupScheduleRequest request) {
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
