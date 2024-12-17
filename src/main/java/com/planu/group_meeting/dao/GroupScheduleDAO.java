package com.planu.group_meeting.dao;

import com.planu.group_meeting.dto.GroupScheduleDTO.scheduleOverViewResponse;
import com.planu.group_meeting.dto.GroupScheduleDTO.todayScheduleResponse;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface GroupScheduleDAO {

    List<todayScheduleResponse> findTodaySchedulesByToday(Long groupId, LocalDateTime today);
    List<scheduleOverViewResponse> findScheduleOverViewsByToday(Long groupId, LocalDateTime today);

    boolean existsScheduleByDate(Long userId, LocalDate date);
}
