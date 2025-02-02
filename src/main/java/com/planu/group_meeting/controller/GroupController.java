package com.planu.group_meeting.controller;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.*;
import com.planu.group_meeting.dto.AvailableDateDto.*;
import com.planu.group_meeting.dto.GroupDTO.*;
import com.planu.group_meeting.service.FriendService;
import com.planu.group_meeting.service.GroupService;
import com.planu.group_meeting.service.GroupUserService;
import com.planu.group_meeting.valid.InputValidator;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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
                                                             @RequestParam("username") String userName) {
        inputValidator.invalidUserNameEquls(userName, userDetails.getUsername());

        return ResponseEntity.ok(groupService.inviteUser(userDetails, userName, id));
    }

    @PutMapping("/join/{groupId}")
    public ResponseEntity<BaseResponse> joinGroup(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @PathVariable("groupId") Long groupId) {

        groupService.joinGroup(userDetails, groupId);


        return BaseResponse.toResponseEntity(HttpStatus.OK, "초대 수락 하였습니다.");
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<GroupResponseDTO>>> groupList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<GroupResponseDTO> groupList = groupService.getGroupList(userDetails.getId());
        ApiResponse<List<GroupResponseDTO>> response = new ApiResponse<>(groupList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/inviteList")
    public ResponseEntity<ApiResponse<List<GroupResponseDTO>>> groupInviteList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<GroupResponseDTO> groupList = groupService.getGroupInviteList(userDetails.getId());
        ApiResponse<List<GroupResponseDTO>> response = new ApiResponse<>(groupList);
        return ResponseEntity.ok(response);
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

    @PatchMapping("/pin/{groupId}")
    public ResponseEntity<BaseResponse> pinedGroup(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @PathVariable("groupId") Long groupId) {
        groupService.pinedGroup(userDetails.getId(), groupId);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "상단 고정/해제 성공");
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<GroupMembersResponse> findGroupMembers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId
    ) {
        GroupMembersResponse groupMembers = new GroupMembersResponse(groupService.findGroupMembers(groupId, userDetails.getId()));
        friendService.setFriendStatus(userDetails.getId(), groupMembers, userDetails.getUsername());
        return ResponseEntity.ok(groupMembers);
    }

    @GetMapping("{groupId}/invite-list")
    public ResponseEntity<NonGroupFriendsResponse> findNonGroupFriends(
            @RequestParam("search") @Nullable String keyword,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId
    ) {
        NonGroupFriendsResponse nonGroupFriends = groupService.getMemberInviteList(groupId, userDetails.getId(), keyword);
        return ResponseEntity.ok(nonGroupFriends);
    }

    @GetMapping("{groupId}/available-dates")
    public ResponseEntity<AvailableDateRatios> findAvailableDateRatios(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth
    ) {
        AvailableDateRatios availableDateRatios = groupService.findAvailableDateRatios(groupId, yearMonth, userDetails.getId());
        return ResponseEntity.ok(availableDateRatios);
    }

    @GetMapping("{groupId}/available-dates/members")
    public ResponseEntity<Map<String, Object>> getAvailableMembers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-mm-dd") @Nullable LocalDate date
    ) {
        if (date == null) {
            date = LocalDate.now();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("availableMembers", groupService.findAvailableMembers(groupId, date, userDetails.getId()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("{groupId}/available-dates/member-info")
    public ResponseEntity<AvailableMemberInfos> getAvailableMemberInfos(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") @Nullable YearMonth yearMonth
    ) {
        if (yearMonth == null) {
            yearMonth = YearMonth.now();
        }

        AvailableMemberInfos memberInfos = groupService.getAvailableMemberInfos(groupId, yearMonth, userDetails.getId());
        return ResponseEntity.ok(memberInfos);
    }

    @GetMapping("{groupId}/available-dates/date-info")
    public ResponseEntity<AvailableDateInfos> getAvailableDateInfos(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") @Nullable YearMonth yearMonth
    ) {
        if (yearMonth == null) {
            yearMonth = YearMonth.now();
        }

        AvailableDateInfos availableDateInfos = groupService.getAvailableDateInfos(groupId, yearMonth, userDetails.getId());
        return ResponseEntity.ok(availableDateInfos);
    }

    @GetMapping("{groupId}/available-dates/ranks")
    public ResponseEntity<List<AvailableDateRanks>> getAvailableDateRanks(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") @Nullable YearMonth yearMonth
    ) {
        if (yearMonth == null) {
            yearMonth = YearMonth.now();
        }
        List<AvailableDateRanks> availableDateRanks = groupService.getAvailableDateRanks(groupId, yearMonth, userDetails.getId());
        return ResponseEntity.ok(availableDateRanks);
    }
}
