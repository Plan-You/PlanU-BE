package com.planu.group_meeting.controller;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.controller.docs.GroupDocs;
import com.planu.group_meeting.dto.AvailableDateDto.AvailableDateRanks;
import com.planu.group_meeting.dto.AvailableDateDto.AvailableDateRatios;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.dto.DataResponse;
import com.planu.group_meeting.dto.GroupDTO;
import com.planu.group_meeting.dto.GroupDTO.AvailableDateInfos;
import com.planu.group_meeting.dto.GroupDTO.AvailableMemberInfos;
import com.planu.group_meeting.dto.GroupDTO.CountOfGroupMembers;
import com.planu.group_meeting.dto.GroupDTO.GroupMembersResponse;
import com.planu.group_meeting.dto.GroupDTO.Member;
import com.planu.group_meeting.dto.GroupDTO.NonGroupFriendsResponse;
import com.planu.group_meeting.dto.GroupInviteResponseDTO;
import com.planu.group_meeting.dto.GroupResponseDTO;
import com.planu.group_meeting.service.FriendService;
import com.planu.group_meeting.service.GroupService;
import com.planu.group_meeting.service.GroupUserService;
import com.planu.group_meeting.valid.InputValidator;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/groups")
public class GroupController implements GroupDocs {

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

    @DeleteMapping("/invite")
    public ResponseEntity<BaseResponse> inviteUserCancel(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @RequestParam("groupId") Long groupId,
                                                         @RequestParam("username") String username) {
        groupService.inviteUserCancel(userDetails.getId(), username, groupId);

        return BaseResponse.toResponseEntity(HttpStatus.OK, "초대 취소 성공");
    }

    @PutMapping("/join/{groupId}")
    public ResponseEntity<BaseResponse> joinGroup(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @PathVariable("groupId") Long groupId) {

        groupService.joinGroup(userDetails, groupId);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "초대 수락 하였습니다.");
    }

    @GetMapping("/list")
    public ResponseEntity<DataResponse<List<GroupResponseDTO>>> groupList(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<GroupResponseDTO> groupList = groupService.getGroupList(userDetails.getId());
        DataResponse<List<GroupResponseDTO>> response = new DataResponse<>(groupList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/inviteList")
    public ResponseEntity<DataResponse<List<GroupResponseDTO>>> groupInviteList(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<GroupResponseDTO> groupList = groupService.getGroupInviteList(userDetails.getId());
        DataResponse<List<GroupResponseDTO>> response = new DataResponse<>(groupList);
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
        groupService.pinnedGroup(userDetails.getId(), groupId);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "상단 고정/해제 성공");
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<GroupMembersResponse> findGroupMembers(@RequestParam("search") @Nullable String keyword,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                                                 @PathVariable("groupId") Long groupId) {
        GroupMembersResponse groupMembers = new GroupMembersResponse(
                groupService.findGroupMembers(groupId, userDetails.getId(), keyword));
        friendService.setFriendStatus(userDetails.getId(), groupMembers, userDetails.getUsername());
        return ResponseEntity.ok(groupMembers);
    }

    @GetMapping("{groupId}/invite-list")
    public ResponseEntity<NonGroupFriendsResponse> findNonGroupFriends(@RequestParam("search") @Nullable String keyword,
                                                                       @AuthenticationPrincipal CustomUserDetails userDetails,
                                                                       @PathVariable("groupId") Long groupId) {
        NonGroupFriendsResponse nonGroupFriends = groupService.getMemberInviteList(groupId, userDetails.getId(),
                keyword);
        return ResponseEntity.ok(nonGroupFriends);
    }

    @GetMapping("{groupId}/available-dates")
    public ResponseEntity<AvailableDateRatios> findAvailableDateRatios(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("groupId") Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        AvailableDateRatios availableDateRatios = groupService.findAvailableDateRatios(groupId, yearMonth,
                userDetails.getId());
        return ResponseEntity.ok(availableDateRatios);
    }

    @GetMapping("{groupId}/available-dates/members")
    public ResponseEntity<Map<String, Object>> getAvailableMembers(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("groupId") Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-mm-dd") @Nullable LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("availableMembers", groupService.findAvailableMembers(groupId, date, userDetails.getId()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("{groupId}/available-dates/member-info")
    public ResponseEntity<AvailableMemberInfos> getAvailableMemberInfos(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("groupId") Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") @Nullable YearMonth yearMonth) {
        if (yearMonth == null) {
            yearMonth = YearMonth.now();
        }

        AvailableMemberInfos memberInfos = groupService.getAvailableMemberInfos(groupId, yearMonth,
                userDetails.getId());
        return ResponseEntity.ok(memberInfos);
    }

    @GetMapping("{groupId}/available-dates/date-info")
    public ResponseEntity<AvailableDateInfos> getAvailableDateInfos(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("groupId") Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") @Nullable YearMonth yearMonth) {
        if (yearMonth == null) {
            yearMonth = YearMonth.now();
        }

        AvailableDateInfos availableDateInfos = groupService.getAvailableDateInfos(groupId, yearMonth,
                userDetails.getId());
        return ResponseEntity.ok(availableDateInfos);
    }
    @GetMapping("{groupId}/available-dates/ranks")
    public ResponseEntity<AvailableDateRanks> getAvailableDateRanks(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("groupId") Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") @Nullable YearMonth yearMonth) {
        if (yearMonth == null) {
            yearMonth = YearMonth.now();
        }
        AvailableDateRanks availableDateRanks = new AvailableDateRanks(
                groupService.getAvailableDateRanks(groupId, yearMonth, userDetails.getId()));
        return ResponseEntity.ok(availableDateRanks);
    }


    @GetMapping("{groupId}/details")
    public ResponseEntity<Map<String, Object>> getGroupDetails(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                               @PathVariable("groupId") Long groupId) {
        GroupDTO.GroupInfo groupInfo = groupService.getGroupDetails(groupId, userDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("groupInfo", groupInfo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("{groupId}/members/count")
    public ResponseEntity<CountOfGroupMembers> getCountOfGroupMembers(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("groupId") Long groupId) {
        List<Member> groupMembers = groupService.findGroupMembers(groupId, userDetails.getId(), null);
        return ResponseEntity.ok(new CountOfGroupMembers(groupMembers.size()));
    }
}
