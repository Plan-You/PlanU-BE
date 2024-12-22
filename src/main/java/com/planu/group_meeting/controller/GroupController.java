package com.planu.group_meeting.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.dto.GroupDTO.GroupDetailResponse;
import com.planu.group_meeting.dto.GroupInviteResponseDTO;
import com.planu.group_meeting.dto.GroupResponseDTO;
import com.planu.group_meeting.service.GroupScheduleService;
import com.planu.group_meeting.service.GroupService;
import com.planu.group_meeting.valid.InputValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;
    private final InputValidator inputValidator;
    private final GroupScheduleService groupScheduleService;

    @PostMapping("/create")
    public ResponseEntity<GroupResponseDTO> createGroup(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                        @RequestParam("groupName") String groupName,
                                                        @RequestParam("groupImage") MultipartFile groupImage) {
        inputValidator.groupNameValid(groupName);
        inputValidator.groupImageValid(groupImage);
        return ResponseEntity.ok(groupService.createGroup(userDetails.getUsername(), groupName, groupImage));
    }

    @PostMapping("/invite")
    public ResponseEntity<GroupInviteResponseDTO> inviteUser(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                             @RequestParam("groupId") Long id,
                                                             @RequestParam("userName") String userName) {
        inputValidator.invalidUserNameEquls(userName, userDetails.getUsername());

        return ResponseEntity.ok(groupService.inviteUser(userDetails, userName, id));
    }

    @PostMapping("/join")
    public ResponseEntity<Map<String, String>> joinGroup(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @RequestParam("groupId") Long id) {

        groupService.joinGroup(userDetails, id);

        Map<String, String> response = new HashMap<>();
        response.put("status", "초대 수락 하였습니다.");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<GroupResponseDTO>> groupList(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(groupService.getGroupList(userDetails.getId()));
    }

    @GetMapping("/inviteList")
    public ResponseEntity<List<GroupResponseDTO>> groupInviteList(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(groupService.getGroupInviteList(userDetails.getId()));
    }

    @PostMapping("/groups/leave/{groupId}")
    public ResponseEntity<BaseResponse> leaveGroup(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @PathVariable("groupId") Long groupId) {
        groupService.leaveGroup(userDetails.getId(), groupId);

        return BaseResponse.toResponseEntity(HttpStatus.OK, "그룹 탈퇴 성공");
    }


    @GetMapping("/{groupId}/detail")
    public ResponseEntity<GroupDetailResponse> groupDetail(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @PathVariable("groupId") Long groupId) {
        LocalDateTime today = LocalDateTime.now();
        System.out.println(groupService.findNameByGroupId(groupId));
        System.out.println(groupScheduleService.findTodaySchedulesByToday(groupId, today));
        System.out.println(groupScheduleService.findScheduleOverViewByToday(groupId, today));

        return ResponseEntity.ok(new GroupDetailResponse(
                groupService.findNameByGroupId(groupId),
                groupScheduleService.findTodaySchedulesByToday(groupId, today),
                groupScheduleService.findScheduleOverViewByToday(groupId, today)
        ));
    }
}
