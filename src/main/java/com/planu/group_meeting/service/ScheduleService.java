package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.*;
import com.planu.group_meeting.dto.ScheduleDto;
import com.planu.group_meeting.dto.ScheduleDto.*;
import com.planu.group_meeting.entity.Schedule;
import com.planu.group_meeting.entity.ScheduleParticipant;
import com.planu.group_meeting.entity.UnregisteredParticipant;
import com.planu.group_meeting.exception.schedule.ScheduleNotFoundException;
import com.planu.group_meeting.exception.user.UnauthorizedResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleDAO scheduleDAO;
    private final GroupScheduleDAO groupScheduleDAO;
    private final UserDAO userDAO;
    private final ParticipantDAO participantDAO;
    private final UnregisteredParticipantDAO unregisteredParticipantDAO;

    @Transactional
    public void createSchedule(Long userId, ScheduleSaveRequest scheduleDto) {
        Schedule schedule = scheduleDto.toEntity(userId);
        scheduleDAO.insertSchedule(schedule);

        insertParticipants(schedule, scheduleDto.getParticipants());
        insertUnregisteredParticipants(schedule, scheduleDto.getUnregisteredParticipants());
    }

    @Transactional
    public void updateSchedule(Long userId, Long scheduleId, ScheduleSaveRequest scheduleSaveRequest) {
        Schedule schedule = scheduleDAO.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("scheduleId " + scheduleId + " : 해당 스케줄을 찾을 수 없습니다."));

        if (!Objects.equals(schedule.getUserId(), userId)) {
            throw new UnauthorizedResourceException();
        }

        schedule.updateSchedule(scheduleSaveRequest);
        scheduleDAO.updateSchedule(schedule);

        participantDAO.deleteAllParticipantsByScheduleId(scheduleId);
        unregisteredParticipantDAO.deleteAllParticipantsByScheduleId(scheduleId);

        insertParticipants(schedule, scheduleSaveRequest.getParticipants());
        insertUnregisteredParticipants(schedule, scheduleSaveRequest.getUnregisteredParticipants());
    }

    @Transactional
    public void deleteSchedule(Long userId, Long scheduleId) {
        Schedule schedule = scheduleDAO.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("scheduleId " + scheduleId + " : 해당 스케줄을 찾을 수 없습니다."));

        if (!Objects.equals(schedule.getUserId(), userId)) {
            throw new UnauthorizedResourceException();
        }

        scheduleDAO.deleteScheduleById(scheduleId);
        participantDAO.deleteAllParticipantsByScheduleId(scheduleId);
        unregisteredParticipantDAO.deleteAllParticipantsByScheduleId(scheduleId);
    }

    public ScheduleDetailsResponse findScheduleDetails(Long scheduleId) {
        return scheduleDAO.getScheduleDetails(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("scheduleId " + scheduleId + " : 해당 스케줄을 찾을 수 없습니다."));
    }

    public DailyScheduleResponse findScheduleList(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        startDate = (startDate != null) ? startDate : today;
        endDate = (endDate != null) ? endDate : startDate;

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<ScheduleListResponse> scheduleList = scheduleDAO.getScheduleList(userId, startDateTime, endDateTime);
        List<ScheduleDto.BirthdayFriend> birthdayFriends = userDAO.findBirthdayByDate(userId, startDate, endDate);

        return new DailyScheduleResponse(scheduleList, birthdayFriends);
    }

    public List<ScheduleCheckResponse> getSchedulesForMonth(Long userId, YearMonth yearMonth) {
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        LocalDate startOfCalendar = firstDayOfMonth.minusDays(firstDayOfMonth.getDayOfWeek().getValue() % 7);
        LocalDate endOfCalendar = lastDayOfMonth.plusDays(6 - lastDayOfMonth.getDayOfWeek().getValue());

        List<ScheduleCheckResponse> scheduleCheckResponse = new ArrayList<>();
        for (LocalDate current = startOfCalendar; !current.isAfter(endOfCalendar); current = current.plusDays(1)) {
            ScheduleCheckResponse response = createScheduleCheckResponse(userId, current);
            if (!response.getScheduleTypes().isEmpty()) {
                scheduleCheckResponse.add(response);
            }
        }

        return scheduleCheckResponse;
    }

    private ScheduleCheckResponse createScheduleCheckResponse(Long userId, LocalDate current) {
        ScheduleCheckResponse response = new ScheduleCheckResponse();
        response.setDate(current);

        if (scheduleDAO.existsScheduleByDate(userId, current)) {
            response.getScheduleTypes().add("personal");
        }
        if (groupScheduleDAO.existsScheduleByDate(userId, current)) {
            response.getScheduleTypes().add("group");
        }
        if (userDAO.existsBirthdayByDate(userId, current)) {
            response.getScheduleTypes().add("birthday");
        }

        return response;
    }

    private void insertParticipants(Schedule schedule, List<Long> participantIds) {
        if (participantIds != null && !participantIds.isEmpty()) {
            List<ScheduleParticipant> participants = participantIds.stream()
                    .map(userId -> new ScheduleParticipant(schedule.getId(), userId))
                    .toList();
            participantDAO.insertScheduleParticipants(participants);
        }
    }

    private void insertUnregisteredParticipants(Schedule schedule, List<String> unregisteredParticipantNames) {
        if (unregisteredParticipantNames != null && !unregisteredParticipantNames.isEmpty()) {
            List<UnregisteredParticipant> unregisteredParticipants = unregisteredParticipantNames.stream()
                    .map(name -> new UnregisteredParticipant(schedule.getId(), name))
                    .toList();
            unregisteredParticipantDAO.insertUnregisteredParticipants(unregisteredParticipants);
        }
    }
}
