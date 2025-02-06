package com.planu.group_meeting.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.dto.NotificationDTO;
import com.planu.group_meeting.service.NotificationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter>subscribeEmitter(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(notificationService.createEmitter(userDetails.getId()));
    }

    @GetMapping("/list")
    public ResponseEntity<List<NotificationDTO>> getNotificationList(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(notificationService.getNotificationList(userDetails.getId()));
    }

}
