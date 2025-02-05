package com.planu.group_meeting.controller;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.dto.GroupScheduleDTO;
import com.planu.group_meeting.dto.GroupScheduleDTO.GroupScheduleRequest;
import com.planu.group_meeting.service.GroupScheduleService;
import com.planu.group_meeting.service.GroupService;
import com.planu.group_meeting.service.GroupUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupScheduleController {

    private final GroupService groupService;
    private final GroupScheduleService groupScheduleService;
    private final GroupUserService groupUserService;

    @PostMapping("{groupId}/schedules")
    public ResponseEntity<BaseResponse> create(@Valid @RequestBody GroupScheduleRequest groupScheduleRequest,
                                               @PathVariable Long groupId,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        groupUserService.isGroupMember(userDetails.getId(), groupId);
        groupScheduleService.insert(groupId, groupScheduleRequest);
        return BaseResponse.toResponseEntity(HttpStatus.CREATED, "그룹 일정 생성 성공");
    }

    @GetMapping("/{groupId}/today")
    public ResponseEntity<GroupScheduleDTO.GroupTodayScheduleResponse> groupTodaySchedule(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                          @PathVariable("groupId") Long groupId) {
        groupUserService.isGroupMember(userDetails.getId(), groupId);
        LocalDateTime today = LocalDateTime.now();
        return ResponseEntity.ok(new GroupScheduleDTO.GroupTodayScheduleResponse(
                groupService.findNameByGroupId(groupId, userDetails.getId()),
                groupScheduleService.findTodaySchedulesByToday(groupId, today)
        ));
    }

    @GetMapping("/{groupId}/calendar")
    public ResponseEntity<GroupScheduleDTO.groupOverViewsResponse> groupRequestSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate endDate
     )
    {
        groupUserService.isGroupMember(userDetails.getId(), groupId);
        return ResponseEntity.ok(new GroupScheduleDTO.groupOverViewsResponse(groupScheduleService.findScheduleOverViewByToday(groupId, startDate, endDate)));
    }

    @GetMapping("/{groupId}/schedules/{scheduleId}")
    public ResponseEntity<GroupScheduleDTO.GroupSchedulesDetailResponse> groupScheduleDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @PathVariable("scheduleId") Long scheduleId
    )
    {
        groupUserService.isGroupMember(userDetails.getId(), groupId);
        return ResponseEntity.ok(groupScheduleService.findByGroupScheduleID(groupId, scheduleId));
    }

    @DeleteMapping("/{groupId}/schedules/{scheduleId}")
    public ResponseEntity<BaseResponse> deleteGroupSchedule(
                    @PathVariable("groupId") Long groupId,
                    @PathVariable("scheduleId") Long scheduleId,
                    @AuthenticationPrincipal CustomUserDetails userDetails
    )
    {
        groupUserService.isGroupMember(userDetails.getId(), groupId);
        groupScheduleService.deleteGroupScheduleById(groupId, scheduleId);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "그룹 일정 삭제 성공");
    }

    @PutMapping("/{groupId}/schedules/{scheduleId}")
    public ResponseEntity<BaseResponse> updateGroupSchedule(
            @PathVariable("groupId") Long groupId,
            @PathVariable("scheduleId") Long scheduleId,
            @Valid @RequestBody GroupScheduleRequest groupScheduleRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails
    )
    {
        groupUserService.isGroupMember(userDetails.getId(), groupId);
        groupScheduleService.updateGroupSchedule(groupId, scheduleId, groupScheduleRequest);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "그룹 일정 수정 성공");
    }
}

