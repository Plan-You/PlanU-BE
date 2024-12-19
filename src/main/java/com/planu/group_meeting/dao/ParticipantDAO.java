package com.planu.group_meeting.dao;

import com.planu.group_meeting.entity.ScheduleParticipant;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ParticipantDAO {
    void insertScheduleParticipants(List<ScheduleParticipant> participants);

    void deleteAllParticipantsByScheduleId(Long scheduleId);
}
