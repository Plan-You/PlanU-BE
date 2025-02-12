package com.planu.group_meeting.service;

import com.planu.group_meeting.entity.GroupSchedule;
import com.planu.group_meeting.entity.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static com.planu.group_meeting.dto.GroupScheduleDTO.GroupScheduleNotification;
import static com.planu.group_meeting.dto.ScheduleDto.ScheduleNotification;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleNotificationService {
    private static final String SCHEDULE_TWO_HOURS_BEFORE_MESSAGE = "일정 2시간 전";
    private static final String SCHEDULE_DAY_BEFORE_MESSAGE = "내일 일정이 있습니다.";

    private final TaskScheduler taskScheduler;
    private final NotificationService notificationService;

    public void reserveScheduleNotification(Schedule schedule) {
        Instant twoHoursBefore = toInstant(schedule.getStartDateTime()).minusSeconds(2 * 60 * 60);
        Instant dayBeforeNoon = toInstant(schedule.getStartDateTime().minusDays(1).with((LocalTime.NOON)));

        log.info("twoHoursBefore={}",twoHoursBefore);
        log.info("dayBeforeNoon={}", dayBeforeNoon);

        scheduleNotification(twoHoursBefore, schedule, SCHEDULE_TWO_HOURS_BEFORE_MESSAGE);
        scheduleNotification(dayBeforeNoon, schedule, SCHEDULE_DAY_BEFORE_MESSAGE);
    }


    public void reserveGroupScheduleNotification(GroupSchedule groupSchedule, List<Long> groupScheduleParticipants){
        Instant twoHoursBefore = toInstant(groupSchedule.getStartDateTime()).minusSeconds(2 * 60 * 60);
        Instant dayBeforeNoon = toInstant(groupSchedule.getStartDateTime().minusDays(1).with((LocalTime.NOON)));

        log.info("twoHoursBefore={}",twoHoursBefore);
        log.info("dayBeforeNoon={}", dayBeforeNoon);

        groupScheduleNotification(twoHoursBefore, groupSchedule, groupScheduleParticipants, SCHEDULE_TWO_HOURS_BEFORE_MESSAGE);
        groupScheduleNotification(dayBeforeNoon, groupSchedule, groupScheduleParticipants, SCHEDULE_DAY_BEFORE_MESSAGE);

    }

    private void scheduleNotification(Instant notificationTime, Schedule schedule, String contents) {
        ScheduleNotification scheduleNotification = new ScheduleNotification(schedule.getUserId(),schedule.getTitle() +System.lineSeparator()+ contents);
        taskScheduler.schedule(()-> {
            log.info("일정 예약 알림 전송");
            notificationService.sendNotification(scheduleNotification.getEventType(),scheduleNotification);
        }, notificationTime);
    }

    private void groupScheduleNotification(Instant notificationTime, GroupSchedule groupSchedule, List<Long> groupScheduleParticipants, String contents) {
        for(Long userId : groupScheduleParticipants){
            GroupScheduleNotification groupScheduleNotification = new GroupScheduleNotification(userId, groupSchedule.getTitle() + System.lineSeparator() + contents);
            taskScheduler.schedule(()-> {
                log.info("그룹 일정 예약 알림 전송");
                notificationService.sendNotification(groupScheduleNotification.getEventType(), groupScheduleNotification);
            }, notificationTime);
        }
    }



    private Instant toInstant(LocalDateTime startDateTime) {
        ZoneId zoneId = ZoneId.of("Asia/Seoul");  // 로컬 시간대 설정
        return startDateTime.atZone(zoneId).toInstant();
    }

}
