package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.*;
import com.planu.group_meeting.dto.GroupScheduleDTO;
import com.planu.group_meeting.dto.GroupScheduleDTO.GroupSchedulesDetailResponse;
import com.planu.group_meeting.dto.GroupScheduleDTO.scheduleOverViewResponse;
import com.planu.group_meeting.dto.GroupScheduleDTO.todayScheduleResponse;
import com.planu.group_meeting.entity.GroupSchedule;
import com.planu.group_meeting.entity.GroupScheduleParticipant;
import com.planu.group_meeting.entity.common.EventType;
import com.planu.group_meeting.exception.group.GroupNotFoundException;
import com.planu.group_meeting.exception.schedule.ScheduleNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static com.planu.group_meeting.dto.GroupScheduleDTO.GroupScheduleRequest;
import static com.planu.group_meeting.dto.GroupScheduleDTO.ParticipantsResponse;
import static com.planu.group_meeting.dto.NotificationDTO.GroupScheduleCreateNotification;
import static com.planu.group_meeting.dto.NotificationDTO.GroupScheduleDeleteNotification;

@Service
@AllArgsConstructor
@Slf4j
public class GroupScheduleService {
    private final GroupScheduleDAO groupScheduleDAO;
    private final GroupScheduleParticipantDAO groupScheduleParticipantDAO;
    private final UserDAO userDAO;
    private final GroupDAO groupDAO;
    private final GroupUserDAO groupUserDAO;
    private final ScheduleNotificationService scheduleNotificationService;
    private final NotificationService notificationService;

    private void checkValidGroupId(Long groupId) {
        if (groupDAO.findGroupById(groupId) == null) {
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
        List<todayScheduleResponse> todaySchedules = groupScheduleDAO.findTodaySchedulesByToday(groupId, today);

        for (var schedule : todaySchedules) {
            String startDate = schedule.getStartDateTime();
            if (startDate.contains("AM")) {
                startDate = startDate.replace("AM", "오전");
            } else if (startDate.contains("PM")) {
                startDate = startDate.replace("PM", "오후");
            }
            schedule.setStartDateTime(startDate);
        }

        return todaySchedules;
    }

    @Transactional
    public List<scheduleOverViewResponse> findScheduleOverViewByToday(Long groupId, LocalDate startDate, LocalDate endDate) {
        checkValidGroupId(groupId);
        if(startDate == null) {
            startDate = LocalDate.now();
        }

        return groupScheduleDAO.findScheduleOverViewsByRange(groupId, startDate);
    }

    @Transactional
    public void insert(Long groupId, @Valid GroupScheduleRequest groupScheduleRequest) {
        checkValidGroupId(groupId);
        GroupSchedule groupSchedule = groupScheduleRequest.toEntity(groupId);
        groupScheduleDAO.insert(groupSchedule);
        List<Long> groupScheduleParticipants = new ArrayList<>();
        for (var username : groupScheduleRequest.getParticipants()) {
            groupScheduleParticipants.add(userDAO.findByUsername(username).getId());
        }
        insertParticipants(groupSchedule, groupScheduleParticipants);

        for(Long groupScheduleParticipant : groupScheduleParticipants){
            GroupScheduleCreateNotification groupScheduleCreateNotification = new GroupScheduleCreateNotification(groupScheduleParticipant, "그룹 일정 '" + groupSchedule.getTitle() + "'이(가) 생성되었습니다.");
            notificationService.sendNotification(EventType.GROUP_SCHEDULE_CREATE, groupScheduleCreateNotification);
        }
        scheduleNotificationService.reserveGroupScheduleNotification(groupSchedule, groupScheduleParticipants);
    }


    private void insertParticipants(GroupSchedule groupSchedule, List<Long> participantIds) {
        if (participantIds != null && !participantIds.isEmpty()) {
            List<GroupScheduleParticipant> participants = participantIds.stream()
                    .map(userId -> new GroupScheduleParticipant(groupSchedule.getId(), userId, groupSchedule.getGroupId()))
                    .toList();
            groupScheduleParticipantDAO.insert(participants);
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

    private List<ParticipantsResponse> findParticipantsByScheduleId(Long groupId, Long scheduleId) {
        checkValidGroupId(groupId);
        findGroupScheduleById(groupId, scheduleId);
        return groupScheduleParticipantDAO.findByScheduleId(groupId, scheduleId);
    }

    @Transactional
    public void deleteGroupScheduleById(Long groupId, Long scheduleId) {
        checkValidGroupId(groupId);
        GroupSchedule groupSchedule = findGroupScheduleById(groupId, scheduleId);

        List<ParticipantsResponse> participantsResponses = groupScheduleParticipantDAO.findByScheduleId(groupId, scheduleId);
        List<Long> participantIds = participantsResponses.stream()
                .map(ParticipantsResponse::getUserId)
                .toList();

        for(Long participantId : participantIds){
            GroupScheduleDeleteNotification groupScheduleDeleteNotification = new GroupScheduleDeleteNotification(participantId, "그룹 일정 '" + groupSchedule.getTitle() + "'이(가) 삭제되었습니다.");
            notificationService.sendNotification(EventType.GROUP_SCHEDULE_DELETE, groupScheduleDeleteNotification);
        }

        groupScheduleParticipantDAO.deleteAllByScheduleId(groupId, scheduleId);
        groupScheduleDAO.deleteGroupScheduleById(groupId, scheduleId);
    }

    @Transactional
    public void updateGroupSchedule(Long groupId, Long scheduleId, @Valid GroupScheduleRequest groupScheduleRequest) {
        checkValidGroupId(groupId);
        GroupSchedule groupSchedule = findGroupScheduleById(groupId, scheduleId);

        groupSchedule.updateGroupSchedule(groupScheduleRequest);
        groupScheduleParticipantDAO.deleteAllByScheduleId(groupId, scheduleId);
        groupScheduleDAO.updateGroupSchedule(groupSchedule);

        List<Long> groupScheduleParticipants = new ArrayList<>();
        for (var username : groupScheduleRequest.getParticipants()) {
            groupScheduleParticipants.add(userDAO.findByUsername(username).getId());
        }

        insertParticipants(groupSchedule, groupScheduleParticipants);
    }

    public List<GroupScheduleDTO.GroupCalendarEvent> getGroupCalendarEvents(Long groupId, YearMonth yearMonth, GroupUserService groupUserService) {
        checkValidGroupId(groupId);

        if (yearMonth == null) {
            yearMonth = YearMonth.now();
        }

        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        LocalDate startOfCalendar = firstDayOfMonth.minusDays(firstDayOfMonth.getDayOfWeek().getValue() % 7);
        LocalDate endOfCalendar = lastDayOfMonth.plusDays(6 - lastDayOfMonth.getDayOfWeek().getValue());

        List<Long> groupMemberIds = groupUserDAO.getGroupMemberIds(groupId);
        List<GroupScheduleDTO.GroupCalendarEvent> groupCalendarEvents = new ArrayList<>();
        for(var current = startOfCalendar; !current.isAfter(endOfCalendar); current = current.plusDays(1)) {
            boolean isSchedule = groupScheduleDAO.existsGroupScheduleByDate(groupId, current);
            boolean isBirthday = false;

            for(var memberId : groupMemberIds) {
                LocalDate birthday = userDAO.findBirthdayById(memberId);
                if(birthday == null) {
                    continue;
                }
                if(current.getMonthValue() == birthday.getMonthValue() && current.getDayOfMonth() == birthday.getDayOfMonth()) {
                    isBirthday = true;
                    break;
                }
            }

            if(isBirthday || isSchedule) {
                groupCalendarEvents.add(new GroupScheduleDTO.GroupCalendarEvent(current.toString(), isSchedule, isBirthday));
            }
        }
        return groupCalendarEvents;
    }
}
