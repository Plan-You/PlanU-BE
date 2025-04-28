package com.planu.group_meeting.location.controller;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.location.controller.docs.LocationControllerDocs;
import com.planu.group_meeting.location.dto.request.LocationDTO;
import com.planu.group_meeting.location.dto.response.GroupMemberLocationResponse;
import com.planu.group_meeting.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class LocationController implements LocationControllerDocs {

    private final SimpMessageSendingOperations messageSender;
    private final LocationService locationService;

    @Transactional
    @MessageMapping("/location/groups/{groupId}/{scheduleId}")
    public void updateLocationAndget(
            LocationDTO locationDTO,
            @DestinationVariable("groupId") Long groupId,
            @DestinationVariable("scheduleId") Long scheduleId,
            StompHeaderAccessor accessor
    ) {
        String username = (String) accessor.getSessionAttributes().get("username");
        locationService.updateLocation(username, locationDTO);
        GroupMemberLocationResponse response = locationService.getGroupMemberLocation(groupId, scheduleId);
        messageSender.convertAndSend("/sub/location/groups/" + groupId + "/" + scheduleId, response);
    }

    @Transactional
    @PostMapping("/users/location/update")
    public ResponseEntity<BaseResponse> updateLocation(
            @RequestBody LocationDTO locationDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        locationService.updateLocation(userDetails.getUsername(), locationDTO);
        return BaseResponse.toResponseEntity(HttpStatus.CREATED, "위치 갱신 성공");
    }

    @Transactional
    @GetMapping("/groups/{groupId}/schedules/{scheduleId}/member-location")
    public ResponseEntity<GroupMemberLocationResponse> getLocation(
            @PathVariable("groupId") Long groupId,
            @PathVariable("scheduleId") Long scheduleId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        GroupMemberLocationResponse response = locationService.getGroupMemberLocation(groupId, scheduleId, userDetails.getId());
        return ResponseEntity.ok(response);
    }
}
