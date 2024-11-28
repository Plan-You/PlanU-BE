package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.ScheduleDAO;
import com.planu.group_meeting.dto.ScheduleDto;
import com.planu.group_meeting.dto.ScheduleDto.ScheduleDetailsResponse;
import com.planu.group_meeting.entity.Schedule;
import com.planu.group_meeting.entity.ScheduleParticipant;
import com.planu.group_meeting.entity.UnregisteredParticipant;
import com.planu.group_meeting.exception.schedule.ScheduleNotFoundException;
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

        if (participantIds != null && !participantIds.isEmpty()) {
            List<ScheduleParticipant> participants = participantIds.stream()
                    .map(userIds -> new ScheduleParticipant(schedule.getId(), userIds))
                    .toList();
            scheduleDAO.insertScheduleParticipants(participants);
        }

        List<String>unregisteredParticipantNames = scheduleDto.getUnregisteredParticipants();
        if(unregisteredParticipantNames !=null && !unregisteredParticipantNames.isEmpty()){
            List<UnregisteredParticipant>unregisteredParticipants = unregisteredParticipantNames.stream()
                    .map(name->new UnregisteredParticipant(schedule.getId(),name))
                    .toList();
            scheduleDAO.insertUnregisteredParticipants(unregisteredParticipants);
        }
    }

    public ScheduleDetailsResponse findScheduleDetails(Long scheduleId){
        ScheduleDetailsResponse scheduleDetails = scheduleDAO.getScheduleDetails(scheduleId);
        if(scheduleDetails==null){
            throw new ScheduleNotFoundException("scheduleId  " + scheduleId + " : 해당하는 스케줄을 찾을 수 없습니다.");
        }
        return scheduleDetails;
    }

}
