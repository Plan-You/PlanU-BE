package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.GroupScheduleDAO;
import com.planu.group_meeting.dao.GroupScheduleParticipantDAO;
import com.planu.group_meeting.dao.GroupScheduleUnregisteredParticipantDAO;
import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.dto.GroupScheduleDTO;
import com.planu.group_meeting.dto.GroupScheduleDTO.scheduleOverViewResponse;
import com.planu.group_meeting.dto.GroupScheduleDTO.todayScheduleResponse;
import com.planu.group_meeting.entity.GroupSchedule;
import com.planu.group_meeting.entity.GroupScheduleParticipant;
import com.planu.group_meeting.entity.GroupScheduleUnregisteredParticipant;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@AllArgsConstructor
public class GroupScheduleService {
    private final GroupScheduleDAO groupScheduleDAO;
    private final GroupScheduleParticipantDAO groupScheduleParticipantDAO;
    private final GroupScheduleUnregisteredParticipantDAO groupScheduleUnregisteredParticipantDAO;
    private final UserDAO userDAO;

    @Transactional
    public List<todayScheduleResponse> findTodaySchedulesByToday(Long groupId, LocalDateTime today) {
        return groupScheduleDAO.findTodaySchedulesByToday(groupId, today);
    }

    @Transactional
    public List<scheduleOverViewResponse> findScheduleOverViewByToday(Long groupId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime today = LocalDateTime.now();
        if(startDate == null || endDate == null) {
            startDate = today.toLocalDate().with(TemporalAdjusters.firstDayOfMonth());
            endDate = today.toLocalDate().with(TemporalAdjusters.lastDayOfMonth());
        }
        return groupScheduleDAO.findScheduleOverViewsByRange(groupId, startDate, endDate);
    }

    @Transactional
    public void insert(Long groupId, GroupScheduleDTO.@Valid GroupScheduleRequest groupScheduleRequest) {
        GroupSchedule groupSchedule = groupScheduleRequest.toEntity(groupId);
        groupScheduleDAO.insert(groupSchedule);

        insertParticipants(groupSchedule, groupScheduleRequest.getParticipants());
        insertUnregisteredParticipants(groupSchedule, groupScheduleRequest.getUnregisteredParticipants());
    }


    private void insertParticipants(GroupSchedule groupSchedule, List<Long> participantIds) {
        if(participantIds != null && !participantIds.isEmpty()) {
            List<GroupScheduleParticipant> participants = participantIds.stream()
                    .map(userId -> new GroupScheduleParticipant(groupSchedule.getId(), userId, groupSchedule.getGroupId()))
                    .toList();
            groupScheduleParticipantDAO.insert(participants);
        }
    }

    private void insertUnregisteredParticipants(GroupSchedule groupSchedule, List<String> unregisteredParticipantNames) {
        if(unregisteredParticipantNames != null && !unregisteredParticipantNames.isEmpty()) {
            List<GroupScheduleUnregisteredParticipant> unregisteredParticipants = unregisteredParticipantNames.stream()
                    .map(userName -> new GroupScheduleUnregisteredParticipant(groupSchedule.getId(), userName))
                    .toList();
            groupScheduleUnregisteredParticipantDAO.insert(unregisteredParticipants);
        }
    }
}
