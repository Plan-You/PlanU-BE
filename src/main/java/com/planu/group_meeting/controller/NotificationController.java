package com.planu.group_meeting.controller;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static com.planu.group_meeting.dto.NotificationDTO.NotificationListResponse;

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
    public ResponseEntity<NotificationListResponse> getNotificationList(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(notificationService.getNotificationList(userDetails.getId()));
    }

    @PostMapping("/read/{notificationId}")
    public ResponseEntity<BaseResponse> readNotification(@PathVariable("notificationId")Long notificationId,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) throws NotFoundException {
        notificationService.readNotification(userDetails.getId(), notificationId);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "알림 읽기 성공");
    }

}
