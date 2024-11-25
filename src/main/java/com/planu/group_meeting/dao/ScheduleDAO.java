package com.planu.group_meeting.dao;

import com.planu.group_meeting.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface ScheduleDAO {
    void insertSchedule(Schedule schedule);

    Schedule findByScheduleId(Long scheduleId);

    void insertScheduleParticipants(@Param("scheduleId") Long scheduleId, @Param("participantIds") List<Long> participantIds);
}
