package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.*;
import com.planu.group_meeting.dto.GroupScheduleDTO;
import com.planu.group_meeting.dto.GroupScheduleDTO.scheduleOverViewResponse;
import com.planu.group_meeting.dto.GroupScheduleDTO.todayScheduleResponse;
import com.planu.group_meeting.dto.GroupScheduleDTO.GroupSchedulesDetailResponse;
import com.planu.group_meeting.entity.GroupSchedule;
import com.planu.group_meeting.entity.GroupScheduleParticipant;
import com.planu.group_meeting.entity.GroupScheduleUnregisteredParticipant;
import com.planu.group_meeting.exception.group.GroupNotFoundException;
import com.planu.group_meeting.exception.schedule.ScheduleNotFoundException;
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
    private final ParticipantDAO participantDAO;
    private final GroupDAO groupDAO;
    private final GroupUserDAO groupUserDAO;

    private void checkValidGroupId(Long groupId) {
          if(groupDAO.findGroupById(groupId) == null) {
              throw new GroupNotFoundException("그룹을 찾을 수 없습니다.");
          }
    }

    private GroupSchedule findGroupScheduleById(Long groupId, Long scheduleId) {
        return groupScheduleDAO.findById(groupId, scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("해당 그룹 일정을 찾을 수 없습니다."));
    }

    @Transactional
    public List<todayScheduleResponse> findTodaySchedulesByToday(Long groupId, LocalDateTime today) {
        checkValidGroupId(groupId);
        return groupScheduleDAO.findTodaySchedulesByToday(groupId, today);
    }

    @Transactional
    public List<scheduleOverViewResponse> findScheduleOverViewByToday(Long groupId, LocalDate startDate, LocalDate endDate) {
        checkValidGroupId(groupId);
        LocalDateTime today = LocalDateTime.now();
        if (startDate == null || endDate == null) {
            startDate = today.toLocalDate().with(TemporalAdjusters.firstDayOfMonth());
            endDate = today.toLocalDate().with(TemporalAdjusters.lastDayOfMonth());
        }
        return groupScheduleDAO.findScheduleOverViewsByRange(groupId, startDate, endDate);
    }

    @Transactional
    public void insert(Long groupId, GroupScheduleDTO.@Valid GroupScheduleRequest groupScheduleRequest) {
        checkValidGroupId(groupId);
        GroupSchedule groupSchedule = groupScheduleRequest.toEntity(groupId);
        groupScheduleDAO.insert(groupSchedule);

        insertParticipants(groupSchedule, groupScheduleRequest.getParticipants());
        insertUnregisteredParticipants(groupSchedule, groupScheduleRequest.getUnregisteredParticipants());
    }


    private void insertParticipants(GroupSchedule groupSchedule, List<Long> participantIds) {
        if (participantIds != null && !participantIds.isEmpty()) {
            List<GroupScheduleParticipant> participants = participantIds.stream()
                    .map(userId -> new GroupScheduleParticipant(groupSchedule.getId(), userId, groupSchedule.getGroupId()))
                    .toList();
            groupScheduleParticipantDAO.insert(participants);
        }
    }

    private void insertUnregisteredParticipants(GroupSchedule groupSchedule, List<String> unregisteredParticipantNames) {
        if (unregisteredParticipantNames != null && !unregisteredParticipantNames.isEmpty()) {
            List<GroupScheduleUnregisteredParticipant> unregisteredParticipants = unregisteredParticipantNames.stream()
                    .map(userName -> new GroupScheduleUnregisteredParticipant(groupSchedule.getId(), userName))
                    .toList();
            groupScheduleUnregisteredParticipantDAO.insert(unregisteredParticipants);
        }
    }

    @Transactional
    public GroupSchedulesDetailResponse findByGroupScheduleID(Long groupId, Long scheduleId) {
        checkValidGroupId(groupId);
        findGroupScheduleById(groupId, scheduleId);
        GroupSchedulesDetailResponse response = groupScheduleDAO.findByScheduleId(groupId, scheduleId);
        response.setParticipants(findParticipantsByScheduleId(groupId, scheduleId));
        return response;
    }

    private List<GroupScheduleDTO.ParticipantsResponse> findParticipantsByScheduleId(Long groupId, Long scheduleId) {
        checkValidGroupId(groupId);
        findGroupScheduleById(groupId, scheduleId);
        List<GroupScheduleDTO.ParticipantsResponse> participants = groupScheduleParticipantDAO.findByScheduleId(groupId, scheduleId);
        participants.addAll(groupScheduleUnregisteredParticipantDAO.findByScheduleId(groupId, scheduleId));
        return participants;
    }

    @Transactional
    public void deleteGroupScheduleById(Long groupId, Long scheduleId) {
        checkValidGroupId(groupId);
        findGroupScheduleById(groupId, scheduleId);
        groupScheduleParticipantDAO.deleteAllByScheduleId(groupId, scheduleId);
        groupScheduleUnregisteredParticipantDAO.deleteAllByScheduleId(groupId, scheduleId);
        groupScheduleDAO.deleteGroupScheduleById(groupId, scheduleId);
    }

    @Transactional
    public void updateGroupSchedule(Long groupId, Long scheduleId, GroupScheduleDTO.@Valid GroupScheduleRequest groupScheduleRequest) {
        checkValidGroupId(groupId);
        GroupSchedule groupSchedule = findGroupScheduleById(groupId, scheduleId);

        groupSchedule.updateGroupSchedule(groupScheduleRequest);
        groupScheduleParticipantDAO.deleteAllByScheduleId(groupId, scheduleId);
        groupScheduleUnregisteredParticipantDAO.deleteAllByScheduleId(groupId, scheduleId);
        groupScheduleDAO.updateGroupSchedule(groupSchedule);

        insertParticipants(groupSchedule, groupScheduleRequest.getParticipants());
        insertUnregisteredParticipants(groupSchedule, groupScheduleRequest.getUnregisteredParticipants());
    }
}
