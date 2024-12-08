package com.planu.group_meeting.dto;

import com.planu.group_meeting.dto.GroupScheduleDTO.scheduleOverViewResponse;
import com.planu.group_meeting.dto.GroupScheduleDTO.todayScheduleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class GroupDTO {

    @Getter
    @AllArgsConstructor
    public static class GroupDetailResponse {
        private String GroupName;
        private List<todayScheduleResponse> todaySchedules;
        private List<scheduleOverViewResponse> groupSchedules;
    }

}
