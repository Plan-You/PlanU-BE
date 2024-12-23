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

import java.time.LocalDateTime;
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
    public List<scheduleOverViewResponse> findScheduleOverViewByToday(Long groupId, LocalDateTime today) {
        return groupScheduleDAO.findScheduleOverViewsByToday(groupId, today);
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

            System.out.println("디버그: " + unregisteredParticipants);
            groupScheduleUnregisteredParticipantDAO.insert(unregisteredParticipants);
        }
    }
}
