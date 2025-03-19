package com.planu.group_meeting.location.controller;

import com.planu.group_meeting.location.dto.request.LocationDTO;
import com.planu.group_meeting.location.dto.response.GroupMemberLocationResponse;
import com.planu.group_meeting.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

@Controller
@RequiredArgsConstructor
public class LocationController {

    private final SimpMessageSendingOperations messageSender;
    private final LocationService locationService;

    @Transactional
    @MessageMapping("/location/groups/{groupId}")
    public void updateLocationAndget(
            LocationDTO locationDTO,
            @DestinationVariable("groupId") Long groupId,
            StompHeaderAccessor accessor
    ) {
        String username = (String) accessor.getSessionAttributes().get("username");
        locationService.updateLocation(username, locationDTO);
        GroupMemberLocationResponse response = locationService.getGroupMemberLocation(groupId);
        messageSender.convertAndSend("/sub/location/groups/" + groupId, response);
    }
}
