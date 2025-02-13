package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.NotificationDAO;
import com.planu.group_meeting.dto.NotificationDTO;
import com.planu.group_meeting.entity.common.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.planu.group_meeting.dto.FriendDto.FriendNotification;
import static com.planu.group_meeting.dto.GroupScheduleDTO.GroupScheduleNotification;
import static com.planu.group_meeting.dto.ScheduleDto.ScheduleNotification;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private static final Long SSE_TIMEOUT = 60L * 1000 * 60;    // 1시간

    private final NotificationDAO notificationDAO;

    public SseEmitter createEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        emitters.put(userId, emitter);
        configEmitter(userId, emitter);

        try {
            emitter.send(SseEmitter.event()
                    .id("DUMMY_EVENT")
                    .data("Connected"));
        } catch (IOException e) {
            emitters.remove(userId);
        }
        return emitter;
    }

    private void configEmitter(Long userId, SseEmitter emitter) {
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));
    }

    public void sendNotification(EventType eventType, Object object) {
        NotificationDTO notification = createNotification(eventType, object);
        log.info("notification={}", notification);
        if (notification == null || notification.getReceiverId() == null) {
            return;
        }
        notificationDAO.save(notification);
        SseEmitter emitter = emitters.get(notification.getReceiverId());

        if (emitter == null) {
            return;
        }

        try {
            emitter.send(SseEmitter.event()
                    .id(notification.getReceiverId().toString())
                    .data(notification));
        } catch (IOException e) {
            emitters.remove(notification.getReceiverId());
        }
    }

    private NotificationDTO createNotification(EventType eventType, Object object) {
        if (eventType == EventType.DUMMY) {
            return NotificationDTO.builder()
                    .eventType(EventType.DUMMY)
                    .contents("Dummy Notification")
                    .receiverId(0L)
                    .build();
        }
        if (object instanceof FriendNotification friendNotification) {
            return NotificationDTO.builder()
                    .eventType(eventType)
                    .senderId(friendNotification.getFromUserId())
                    .receiverId(friendNotification.getToUserId())
                    .contents(friendNotification.getContents())
                    .build();
        }
        if (object instanceof ScheduleNotification scheduleNotification) {
            return NotificationDTO.builder()
                    .eventType(eventType)
                    .senderId(scheduleNotification.getSenderId())
                    .receiverId(scheduleNotification.getReceiverId())
                    .contents(scheduleNotification.getContents())
                    .build();
        }

        if(object instanceof GroupScheduleNotification groupScheduleNotification){
            return NotificationDTO.builder()
                    .eventType(eventType)
                    .senderId(groupScheduleNotification.getSenderId())
                    .receiverId(groupScheduleNotification.getReceiverId())
                    .contents(groupScheduleNotification.getContents())
                    .build();
        }
        return null;
    }

    public List<NotificationDTO> getNotificationList(Long userId) {
        return notificationDAO.findAllByUserId(userId);
    }

    public void readNotification(Long userId, Long notificationId) throws NotFoundException {
        notificationDAO.findById(userId, notificationId)
                .orElseThrow(()-> new NotFoundException("해당 알림이 존재하지 않습니다."));

        notificationDAO.updateIsRead(notificationId);
    }

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void deleteOldNotification() {
        notificationDAO.deleteOldNotification();
    }
}