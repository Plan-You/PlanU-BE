package com.planu.group_meeting.dao;

import com.planu.group_meeting.dto.ScheduleDto;
import com.planu.group_meeting.dto.ScheduleDto.ScheduleListResponse;
import com.planu.group_meeting.dto.ScheduleDto.ScheduleDetailsResponse;
import com.planu.group_meeting.entity.Schedule;
import com.planu.group_meeting.entity.ScheduleParticipant;
import com.planu.group_meeting.entity.UnregisteredParticipant;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface ScheduleDAO {
    void insertSchedule(Schedule schedule);

    void updateSchedule(Schedule schedule);

    Optional<Schedule> findById(Long scheduleId);

    ScheduleDetailsResponse getScheduleDetails(Long scheduleId);

    List<ScheduleListResponse> getScheduleList(@Param("userId") Long userId,
                                               @Param("startDateTime") LocalDateTime startDateTime,
                                               @Param("endDateTime") LocalDateTime endDateTime);

    boolean existsScheduleByDate(Long userId, LocalDate date);

}
