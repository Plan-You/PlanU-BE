package com.planu.group_meeting.controller;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.dto.GroupScheduleDTO.GroupScheduleRequest;
import com.planu.group_meeting.service.GroupScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupScheduleController {

    private final GroupScheduleService groupScheduleService;

    @PostMapping("{groupId}/schedules")
    public ResponseEntity<BaseResponse> create(@Valid @RequestBody GroupScheduleRequest groupScheduleRequest,
                                               @PathVariable Long groupId,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        groupScheduleService.insert(groupId, groupScheduleRequest);
        return BaseResponse.toResponseEntity(HttpStatus.CREATED, "그룹 일정 생성 성공");
    }
}

