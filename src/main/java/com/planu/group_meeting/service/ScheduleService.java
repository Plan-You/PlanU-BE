package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.ScheduleDAO;
import com.planu.group_meeting.dto.ScheduleDto;
import com.planu.group_meeting.entity.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleDAO scheduleDAO;

    @Transactional
    public void createSchedule(Long userId, ScheduleDto.ScheduleSaveRequest scheduleDto) {
        Schedule schedule = scheduleDto.toEntity(userId);
        scheduleDAO.insertSchedule(schedule);
        List<Long> participantIds = scheduleDto.getParticipants();
        if(participantIds!=null && !participantIds.isEmpty()){
            scheduleDAO.insertScheduleParticipants(schedule.getId(), participantIds);
        }
    }

}
