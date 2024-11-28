package com.planu.group_meeting.dao;

import com.planu.group_meeting.dto.ScheduleDto;
import com.planu.group_meeting.dto.ScheduleDto.ScheduleDetailsResponse;
import com.planu.group_meeting.entity.Schedule;
import com.planu.group_meeting.entity.ScheduleParticipant;
import com.planu.group_meeting.entity.UnregisteredParticipant;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ScheduleDAO {
    void insertSchedule(Schedule schedule);

    void insertScheduleParticipants(List<ScheduleParticipant> participants);

    void insertUnregisteredParticipants(List<UnregisteredParticipant> unregisteredParticipants);

    ScheduleDetailsResponse getScheduleDetails(Long scheduleId);
}
