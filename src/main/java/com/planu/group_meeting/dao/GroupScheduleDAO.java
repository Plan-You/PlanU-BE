package com.planu.group_meeting.dao;

import com.planu.group_meeting.dto.GroupScheduleDTO.scheduleOverViewResponse;
import com.planu.group_meeting.dto.GroupScheduleDTO.todayScheduleResponse;
import com.planu.group_meeting.entity.GroupSchedule;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface GroupScheduleDAO {

    List<todayScheduleResponse> findTodaySchedulesByToday(Long groupId, LocalDateTime today);

    List<scheduleOverViewResponse> findScheduleOverViewsByRange(Long groupId, LocalDate startDate, LocalDate endDate);

    boolean existsScheduleByDate(Long userId, LocalDate date);

    void insert(GroupSchedule groupSchedule);
}
