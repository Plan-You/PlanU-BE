package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.*;
import com.planu.group_meeting.entity.Schedule;
import com.planu.group_meeting.entity.ScheduleParticipant;
import com.planu.group_meeting.entity.UnregisteredParticipant;
import com.planu.group_meeting.entity.User;
import com.planu.group_meeting.entity.common.FriendStatus;
import com.planu.group_meeting.exception.schedule.ScheduleNotFoundException;
import com.planu.group_meeting.exception.user.NotFoundUserException;
import com.planu.group_meeting.exception.user.NotFriendException;
import com.planu.group_meeting.exception.user.UnauthorizedResourceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.planu.group_meeting.dto.ScheduleDto.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    private final ScheduleDAO scheduleDAO;
    private final GroupScheduleDAO groupScheduleDAO;
    private final UserDAO userDAO;
    private final FriendDAO friendDAO;
    private final ParticipantDAO participantDAO;
    private final UnregisteredParticipantDAO unregisteredParticipantDAO;
    private final ScheduleNotificationService scheduleNotificationService;

    @Transactional
    public void createSchedule(Long userId, ScheduleSaveRequest scheduleDto) {
        if (scheduleDto.getStartDateTime().isAfter(scheduleDto.getEndDateTime())) {
            throw new IllegalArgumentException("시작 날짜와 시간은 종료 날짜와 시간보다 늦을 수 없습니다.");
        }
        Schedule schedule = scheduleDto.toEntity(userId);
        List<Long> participantIds = getParticipantIds(scheduleDto.getParticipants());
        validateParticipants(userId, participantIds);

        scheduleDAO.insertSchedule(schedule);
        insertParticipants(schedule, participantIds);
        insertUnregisteredParticipants(schedule, scheduleDto.getUnregisteredParticipants());

        scheduleNotificationService.reserveScheduleNotification(schedule);

    }

    private List<Long> getParticipantIds(List<String> participants) {
        return participants.stream()
                .map(username -> {
                    User user = userDAO.findByUsername(username);
                    if (user == null) {
                        throw new NotFoundUserException();
                    }
                    return user.getId();
                })
                .toList();
    }

    private void validateParticipants(Long userId, List<Long> participants) {
        for (Long participantId : participants) {
            if (friendDAO.getFriendStatus(userId, participantId) != FriendStatus.FRIEND) {
                throw new NotFriendException();
            }
        }
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
        insertParticipants(schedule, getParticipantIds(scheduleSaveRequest.getParticipants()));
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

    public DailyScheduleResponse findScheduleList(String username, LocalDate startDate, LocalDate endDate) {
        User targetUser = userDAO.findByUsername(username);
        if(targetUser==null){
            throw new NotFoundUserException();
        }
        Long targetUserId = targetUser.getId();

        LocalDate today = LocalDate.now();
        startDate = (startDate != null) ? startDate : today;
        endDate = (endDate != null) ? endDate : startDate;

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<ScheduleListResponse> scheduleList = scheduleDAO.getScheduleList(targetUserId, startDateTime, endDateTime);
        normalizeScheduleTimes(startDate, endDate, scheduleList);
        List<BirthdayPerson> birthdayFriends = userDAO.findFriendsAndGroupMembersBirthdays(targetUserId, startDate, endDate);

        return new DailyScheduleResponse(scheduleList, birthdayFriends);
    }

    private static void normalizeScheduleTimes(LocalDate startDate, LocalDate endDate, List<ScheduleListResponse> scheduleList) {
        for(ScheduleListResponse scheduleListResponse : scheduleList){
            LocalDateTime startTime = scheduleListResponse.getStartTime();
            LocalDateTime endTime = scheduleListResponse.getEndTime();

            if(startDate.isAfter(startTime.toLocalDate())){
                scheduleListResponse.setStartTime(startDate.atStartOfDay());
            }

            if(endDate.isAfter(endTime.toLocalDate())){
                scheduleListResponse.setEndTime(endDate.atTime(LocalTime.MAX));
            }
        }
    }

    public List<ScheduleCheckResponse> getSchedulesForMonth(Long currentUserId, String username, YearMonth yearMonth) {
        User targetUser = userDAO.findByUsername(username);
        if(targetUser==null){
            throw new NotFoundUserException();
        }
        Long targetUserId = targetUser.getId();

        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        LocalDate startOfCalendar = firstDayOfMonth.minusDays(firstDayOfMonth.getDayOfWeek().getValue() % 7);
        LocalDate endOfCalendar = lastDayOfMonth.plusDays(6 - lastDayOfMonth.getDayOfWeek().getValue());

        List<ScheduleCheckResponse> scheduleCheckResponse = new ArrayList<>();
        for (LocalDate current = startOfCalendar; !current.isAfter(endOfCalendar); current = current.plusDays(1)) {
            ScheduleCheckResponse response = createScheduleCheckResponse(targetUserId, current);
            if (existsEvent(response)) {
                scheduleCheckResponse.add(response);
            }
        }

        return scheduleCheckResponse;
    }

    private boolean existsEvent(ScheduleCheckResponse response) {
        return response.isSchedule() || response.isGroupSchedule() || response.isBirthday();
    }

    private ScheduleCheckResponse createScheduleCheckResponse(Long userId, LocalDate current) {
        ScheduleCheckResponse response = new ScheduleCheckResponse();
        response.setDate(current);

        if (scheduleDAO.existsScheduleByDate(userId, current)) {
            response.setSchedule(true);
        }
        if (groupScheduleDAO.existsScheduleByDate(userId, current)) {
            response.setGroupSchedule(true);
        }
        if (userDAO.existsBirthdayByDate(userId, current)) {
            response.setBirthday(true);
        }

        return response;
    }

    private void insertParticipants(Schedule schedule, List<Long> participantIds) {
        if (participantIds != null && !participantIds.isEmpty()) {
            List<ScheduleParticipant> scheduleParticipants = participantIds.stream()
                    .map(participant -> new ScheduleParticipant(schedule.getId(), participant))
                    .toList();
            participantDAO.insertScheduleParticipants(scheduleParticipants);
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
