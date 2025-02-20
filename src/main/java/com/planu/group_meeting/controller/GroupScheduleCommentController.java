package com.planu.group_meeting.controller;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.dto.GroupScheduleCommentDTO;
import com.planu.group_meeting.service.GroupScheduleCommentService;
import com.planu.group_meeting.service.GroupScheduleService;
import com.planu.group_meeting.service.GroupUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("groups/{groupId}/schedules/{scheduleId}")
public class GroupScheduleCommentController {

    private final GroupUserService groupUserService;
    private final GroupScheduleService groupScheduleService;
    private final GroupScheduleCommentService groupScheduleCommentService;

    @PostMapping("/comment")
    public ResponseEntity<BaseResponse> create(
            @PathVariable("groupId") Long groupId,
            @PathVariable("scheduleId") Long groupScheduleId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody GroupScheduleCommentDTO.GroupScheduleCommentRequest groupScheduleComment
    ) {
        groupUserService.isGroupMember(userDetails.getId(), groupId);
        groupScheduleService.isValidSchedule(groupId, groupScheduleId);
        groupScheduleCommentService.create(userDetails.getId(), groupId, groupScheduleId, groupScheduleComment);
        return BaseResponse.toResponseEntity(HttpStatus.CREATED, "그룹 일정 댓글 생성 성공");
    }

    @GetMapping("/comment")
    public ResponseEntity<Map<String, Object>> getAllComments(
       @PathVariable("groupId") Long groupId,
       @PathVariable("scheduleId") Long groupScheduleId,
       @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        groupUserService.isGroupMember(userDetails.getId(), groupId);
        groupScheduleService.isValidSchedule(groupId, groupScheduleId);

        Map<String, Object> response = groupScheduleCommentService.getAllByGroupScheduleId(groupId, groupScheduleId);

        return ResponseEntity.ok(response);
    }
}
