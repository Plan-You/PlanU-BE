package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.GroupScheduleDAO;
import com.planu.group_meeting.dao.ScheduleDAO;
import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.dto.ScheduleDto;
import com.planu.group_meeting.dto.ScheduleDto.DailyScheduleResponse;
import com.planu.group_meeting.dto.ScheduleDto.ScheduleCheckResponse;
import com.planu.group_meeting.dto.ScheduleDto.ScheduleListResponse;
import com.planu.group_meeting.dto.ScheduleDto.ScheduleDetailsResponse;
import com.planu.group_meeting.entity.Schedule;
import com.planu.group_meeting.entity.ScheduleParticipant;
import com.planu.group_meeting.entity.UnregisteredParticipant;
import com.planu.group_meeting.exception.schedule.ScheduleNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleDAO scheduleDAO;
    private final GroupScheduleDAO groupScheduleDAO;
    private final UserDAO userDAO;

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

        List<String> unregisteredParticipantNames = scheduleDto.getUnregisteredParticipants();
        if (unregisteredParticipantNames != null && !unregisteredParticipantNames.isEmpty()) {
            List<UnregisteredParticipant> unregisteredParticipants = unregisteredParticipantNames.stream()
                    .map(name -> new UnregisteredParticipant(schedule.getId(), name))
                    .toList();
            scheduleDAO.insertUnregisteredParticipants(unregisteredParticipants);
        }
    }

    public ScheduleDetailsResponse findScheduleDetails(Long scheduleId) {
        ScheduleDetailsResponse scheduleDetails = scheduleDAO.getScheduleDetails(scheduleId);
        if (scheduleDetails == null) {
            throw new ScheduleNotFoundException("scheduleId  " + scheduleId + " : 해당하는 스케줄을 찾을 수 없습니다.");
        }
        return scheduleDetails;
    }

    public DailyScheduleResponse findScheduleList(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        startDate = (startDate != null) ? startDate : today;
        endDate = (endDate != null) ? endDate : startDate;

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<ScheduleListResponse>scheduleList = scheduleDAO.getScheduleList(userId, startDateTime, endDateTime);
        List<ScheduleDto.BirthdayFriend>birthdayFriends = userDAO.findBirthdayByDate(userId,startDate,endDate);
        DailyScheduleResponse response = new DailyScheduleResponse();
        response.setSchedules(scheduleList);
        response.setBirthdayFriends(birthdayFriends);

        return response;
    }

    public List<ScheduleCheckResponse> getSchedulesForMonth(Long userId, YearMonth yearMonth) {
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        LocalDate startOfCalendar = firstDayOfMonth.minusDays(firstDayOfMonth.getDayOfWeek().getValue() % 7);
        LocalDate endOfCalendar = lastDayOfMonth.plusDays(6 - lastDayOfMonth.getDayOfWeek().getValue());

        LocalDate current = startOfCalendar;

        List<ScheduleCheckResponse> scheduleCheckResponse = new ArrayList<>();

        while (!current.isAfter(endOfCalendar)) {
            ScheduleCheckResponse response = new ScheduleCheckResponse();
            response.setDate(current);
            // 개인일정 추가
            if (scheduleDAO.existsScheduleByDate(userId, current)) {
                response.getScheduleTypes().add("personal");
            }
            // 그룹일정 추가
            if (groupScheduleDAO.existsScheduleByDate(userId, current)) {
                response.getScheduleTypes().add("group");
            }
            // 생일일정 추가
            if (userDAO.existsBirthdayByDate(userId, current)) {
                response.getScheduleTypes().add("birthday");
            }
            if (!response.getScheduleTypes().isEmpty()) {
                scheduleCheckResponse.add(response);
            }
            current = current.plusDays(1);
        }
        return scheduleCheckResponse;

    }


}
