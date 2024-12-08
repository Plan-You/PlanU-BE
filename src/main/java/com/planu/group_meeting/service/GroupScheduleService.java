package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.GroupScheduleDAO;
import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.dto.GroupScheduleDTO.scheduleOverViewResponse;
import com.planu.group_meeting.dto.GroupScheduleDTO.todayScheduleResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class GroupScheduleService {
    private final GroupScheduleDAO groupScheduleDAO;
    private final UserDAO userDAO;

    @Transactional
    public List<todayScheduleResponse> findTodaySchedulesByToday(Long groupId, LocalDateTime today) {
        return groupScheduleDAO.findTodaySchedulesByToday(groupId, today);
    }

    @Transactional
    public List<scheduleOverViewResponse> findScheduleOverViewByToday(Long groupId, LocalDateTime today) {
        return groupScheduleDAO.findScheduleOverViewsByToday(groupId, today);
    }
}
