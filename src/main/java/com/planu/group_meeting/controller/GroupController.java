package com.planu.group_meeting.controller;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.AvailableDateDto.AvailableDateRatios;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.dto.GroupDTO.GroupMembersResponse;
import com.planu.group_meeting.dto.GroupDTO.NonGroupFriendsResponse;
import com.planu.group_meeting.dto.GroupInviteResponseDTO;
import com.planu.group_meeting.dto.GroupResponseDTO;
import com.planu.group_meeting.service.FriendService;
import com.planu.group_meeting.service.GroupService;
import com.planu.group_meeting.service.GroupUserService;
import com.planu.group_meeting.valid.InputValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;
    private final FriendService friendService;
    private final InputValidator inputValidator;
    private final GroupUserService groupUserService;

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

    @PutMapping("/join/{groupId}")
    public ResponseEntity<Map<String, String>> joinGroup(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @PathVariable("groupId") Long groupId) {

        groupService.joinGroup(userDetails, groupId);

        Map<String, String> response = new HashMap<>();
        response.put("status", "초대 수락 하였습니다.");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<GroupResponseDTO>> groupList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(groupService.getGroupList(userDetails.getId()));
    }

    @GetMapping("/inviteList")
    public ResponseEntity<List<GroupResponseDTO>> groupInviteList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(groupService.getGroupInviteList(userDetails.getId()));
    }

    @DeleteMapping("/leave/{groupId}")
    public ResponseEntity<BaseResponse> leaveGroup(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @PathVariable("groupId") Long groupId) {
        groupService.leaveGroup(userDetails.getId(), groupId);

        return BaseResponse.toResponseEntity(HttpStatus.OK, "그룹 탈퇴 성공");
    }

    @DeleteMapping("/delete/{groupId}")
    public ResponseEntity<BaseResponse> deleteGroup(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @PathVariable("groupId") Long groupId) {
        groupService.deleteGroup(userDetails.getId(), groupId);

        return BaseResponse.toResponseEntity(HttpStatus.OK, "그룹 삭제 성공");
    }

    @DeleteMapping("/decline/{groupId}")
    public ResponseEntity<BaseResponse> declineInvitation(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                          @PathVariable("groupId") Long groupId) {
        groupService.declineInvitation(userDetails.getId(), groupId);

        return BaseResponse.toResponseEntity(HttpStatus.OK, "초대 거절 성공");
    }

    @DeleteMapping("/{groupId}/members/{username}")
    public ResponseEntity<BaseResponse> forceExpelMember(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @PathVariable("groupId") Long groupId,
                                                         @PathVariable("username") String username) {
        groupService.forceExpelMember(userDetails.getId(), groupId, username);

        return BaseResponse.toResponseEntity(HttpStatus.OK, "강제 퇴출 성공");
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<GroupMembersResponse> findGroupMembers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId
    ) {
        GroupMembersResponse groupMembers = new GroupMembersResponse(groupService.findGroupMembers(groupId));
        friendService.setFriendStatus(userDetails.getId(), groupMembers, userDetails.getUsername());
        return ResponseEntity.ok(groupMembers);
    }

    @GetMapping("{groupId}/invite-list")
    public ResponseEntity<NonGroupFriendsResponse> findNonGroupFriends (
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable("groupId") Long groupId
    ) {
        NonGroupFriendsResponse nonGroupFriends = groupService.getMemberInviteList(groupId, userDetails.getId());
        return ResponseEntity.ok(nonGroupFriends);
    }

    @GetMapping("{groupId}/available-date")
    public ResponseEntity<AvailableDateRatios> findAvailableDateRatios(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth
    )
    {
        AvailableDateRatios availableDateRatios = groupService.findAvailableDateRatios(groupId, yearMonth, userDetails.getId());
        return ResponseEntity.ok(availableDateRatios);
    }
}
