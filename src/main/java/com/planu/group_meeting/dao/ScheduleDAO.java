package com.planu.group_meeting.dao;

import com.planu.group_meeting.dto.ScheduleDto.ScheduleDetailsResponse;
import com.planu.group_meeting.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.planu.group_meeting.dto.ScheduleDto.ScheduleListResponse;

@Mapper
public interface ScheduleDAO {
    void insertSchedule(Schedule schedule);

    void updateSchedule(Schedule schedule);

    void deleteScheduleById(Long scheduleId);

    Optional<Schedule> findById(Long scheduleId);

    Optional<ScheduleDetailsResponse> getScheduleDetails(Long scheduleId);

    List<ScheduleListResponse> getScheduleList(@Param("userId") Long userId,
                                               @Param("startDateTime") LocalDateTime startDateTime,
                                               @Param("endDateTime") LocalDateTime endDateTime);



    boolean existsScheduleByDate(Long userId, LocalDate date);

}
