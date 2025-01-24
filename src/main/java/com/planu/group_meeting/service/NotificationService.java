package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.NotificationDAO;
import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.dto.NotificationDTO;
import com.planu.group_meeting.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final Map<Long, SseEmitter>emitters = new ConcurrentHashMap<>();
    private final NotificationDAO notificationDAO;

    public SseEmitter createEmitter(Long userId){
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(userId, emitter);
        configEmitter(userId, emitter);
        sendToClient(emitter, userId, userId +": 연결 성공");
        return emitter;
    }

    private void sendToClient(SseEmitter emitter, Long userId, Object data) {
        try{
            emitter.send(SseEmitter.event()
                    .id(userId.toString())
                    .data(data));
        } catch (IOException e){
            emitters.remove(userId);
            throw new IllegalArgumentException();
        }
    }

    private void configEmitter(Long userId, SseEmitter emitter) {
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));
    }

//    public void sendNotification(User receiver, User provider, Object data, String relatedUrl){
//        NotificationDTO notification = notificationDAO.save()
//    }

}
