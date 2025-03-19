package com.planu.group_meeting.location.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

@Controller
@RequiredArgsConstructor
public class LocationController {

    private final SimpMessageSendingOperations messageSender;

    @Transactional
    @MessageMapping("/location/groups/{groupId}")
    public void updateLocationAndget(@DestinationVariable("groupId") Long groupId) {

    }
}
