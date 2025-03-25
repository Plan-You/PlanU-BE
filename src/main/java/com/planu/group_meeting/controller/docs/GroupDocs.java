package com.planu.group_meeting.controller.docs;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.*;
import com.planu.group_meeting.dto.AvailableDateDto.AvailableDateRanks;
import com.planu.group_meeting.dto.AvailableDateDto.AvailableDateRatios;
import com.planu.group_meeting.dto.GroupDTO.AvailableDateInfos;
import com.planu.group_meeting.dto.GroupDTO.AvailableMemberInfos;
import com.planu.group_meeting.dto.GroupDTO.GroupMembersResponse;
import com.planu.group_meeting.dto.GroupDTO.NonGroupFriendsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "GROUP API", description = "그룹 API")
public interface GroupDocs {


    @Operation(summary = "그룹 생성", description = "그룹을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "그룹 생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\n" +
                                    "  \"groupId\": \"group1\",\n" +
                                    "  \"groupName\": \"컴공\",\n" +
                                    "  \"leadeUsername\": \"ehgk4245\",\n" +
                                    "  \"groupIamgeUrl\": \"http://example.com/images/teamx_logo.jpg\"\n" +
                                    "}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "그룹명이 15자를 넘어갈 경우.",
                                            value = "{\n" +
                                                    "  \"ResultCode\": 400,\n" +
                                                    "  \"ResultMsg\": \"그룹명은 15자 이내로 입력해야 합니다.\"\n" +
                                                    "}"),
                                    @ExampleObject(name = "그룹 이미지가 없을 경우.",
                                            value = "{\n" +
                                                    "  \"ResultCode\": 400,\n" +
                                                    "  \"ResultMsg\": \"그룹 이미지는 필수 항목입니다.\"\n" +
                                                    "}"),
                                    @ExampleObject(name = "그룹 이미지 형식이 JPEG, PNG, GIF가 아닌경우.",
                                            value = "{\n" +
                                                    "  \"ResultCode\": 400,\n" +
                                                    "  \"ResultMsg\": \"허용된 이미지 파일 형식은 JPEG, PNG, GIF 입니다.\"\n" +
                                                    "}"),
                                    @ExampleObject(name = "그룹 이미지의 크기가 5MB를 넘을 경우.",
                                            value = "{\n" +
                                                    "  \"ResultCode\": 400,\n" +
                                                    "  \"ResultMsg\": \"그룹 이미지 파일은 5MB를 초과할 수 없습니다.\"\n" +
                                                    "}")
                            })
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "그룹 생성을 위한 데이터. `form-data` 형식으로 요청해야 합니다.",
            content = @Content(
                    mediaType = "multipart/form-data",
                    schema = @Schema(implementation = GroupRequestDTO.class)
            )
    )
    @PostMapping("/create")
    public ResponseEntity<GroupResponseDTO> createGroup(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                        GroupRequestDTO groupRequestDTO);

    @Operation(summary = "그룹 초대", description = "그룹원을 초대합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "그룹 초대 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\n" +
                                    "\t\"invitedUsername\" : \"ehgk4245\",\n" +
                                    "\t\"groupId\" : 1\n" +
                                    "\t\"groupName\" : \"가나다\"\n" +
                                    "\t\"groupImageUrl\" : \"http://example.com/images/teamx_logo.jpg\"\n" +
                                    "}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "그룹에 속해있지 않는유저가 초대하는 경우",
                                            value = "{\n" +
                                                    "  \"ResultCode\": 400,\n" +
                                                    "  \"ResultMsg\": \"초대 권한이 없습니다.\"\n" +
                                                    "}"),
                                    @ExampleObject(name = "자기 자신을 초대한 경우",
                                            value = "{\n" +
                                                    "  \"ResultCode\": 400,\n" +
                                                    "  \"ResultMsg\": \"자기 자신을 초대할 수 없습니다.\"\n" +
                                                    "}"),
                                    @ExampleObject(name = "초대받을 유저가 없을 경우",
                                            value = "{\n" +
                                                    "  \"ResultCode\": 400,\n" +
                                                    "  \"ResultMsg\": \"사용자 '\" + userName + \"'을 찾을 수 없습니다.\"\n" +
                                                    "}"),
                                    @ExampleObject(name = "초대하려는 그룹이 존재하지 않을 경우",
                                            value = "{\n" +
                                                    "  \"ResultCode\": 400,\n" +
                                                    "  \"ResultMsg\": \"해당 그룹이 존재하지 않습니다.\"\n" +
                                                    "}")
                            })
            )
    })
    ResponseEntity<GroupInviteResponseDTO> inviteUser(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                      @RequestParam("groupId") Long id,
                                                      @RequestParam("username") String userName);

    @Operation(summary = "그룹 초대 취소", description = "그룹 리더가 보낸 초대를 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "초대 취소 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\n" +
                                    "\t\"resultCode\": 200,\n" +
                                    "  \"resultMsg\": \"초대 취소 성공\"\n" +
                                    "}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "초대 거절 권한이 없을 경우.",
                                            value = "{\n" +
                                                    "  \"ResultCode\": 400,\n" +
                                                    "  \"ResultMsg\": \"초대 권한이 없습니다.\"\n" +
                                                    "}"),
                                    @ExampleObject(name = "해당 그룹이 존재하지 않을 경우.",
                                            value = "{\n" +
                                                    "  \"ResultCode\": 400,\n" +
                                                    "  \"ResultMsg\": \"해당 그룹이 존재하지 않습니다.\"\n" +
                                                    "}"),
                                    @ExampleObject(name = "이미 취소 되었거나 유저가 없는경우",
                                            value = "{\n" +
                                                    "  \"ResultCode\": 400,\n" +
                                                    "  \"ResultMsg\": \"사용자 '\" + userName + \"'을 찾을 수 없습니다.\"\n" +
                                                    "}"),
                                    @ExampleObject(name = "해당 유저가 이미 그룹초대를 수락한 경우",
                                            value = "{\n" +
                                                    "  \"ResultCode\": 400,\n" +
                                                    "  \"ResultMsg\": \"사용자 '\" + username + \"'은 이미 그룹의 멤버입니다.\"\n" +
                                                    "}")
                            })
            )
    })
    ResponseEntity<BaseResponse> inviteUserCancel(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @RequestParam("groupId") Long groupId,
                                                  @RequestParam("username") String username);

    @Operation(summary = "초대 수락", description = "그룹 초대를 수락 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "초대 수락 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\n" +
                                    "\t\"resultCode\": 200,\n" +
                                    "  \"resultMsg\": \"초대 수락 하였습니다.\"\n" +
                                    "}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "그룹을 찾을 수 없는 경우.",
                                            value = "{\n" +
                                                    "  \"resultCode\": 400,\n" +
                                                    "  \"resultMsg\": \"해당 그룹이 존재하지 않습니다.\"\n" +
                                                    "}"),
                                    @ExampleObject(name = "초대 받지 않은경우.",
                                            value = "{\n" +
                                                    "  \"resultCode\": 400,\n" +
                                                    "  \"resultMsg\": \"초대 받지 않았습니다.\"\n" +
                                                    "}"),
                                    @ExampleObject(name = "이미 그룹에 있는 경우.",
                                            value = "{\n" +
                                                    "  \"resultCode\": 400,\n" +
                                                    "  \"resultMsg\": \"이미 그룹에 속해 있습니다.\"\n" +
                                                    "}")
                            })
            )
    })
    ResponseEntity<BaseResponse> joinGroup(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @PathVariable("groupId") Long groupId);

    @Operation(summary = "그룹 조회", description = "사용자가 속한 그룹 목록을 보여줍니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "그룹 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "[\n" +
                                    "  \"data\" : [\n" +
                                    "\t  {\n" +
                                    "\t    \"groupId\": 1,\n" +
                                    "\t    \"groupName\": \"Study Group\",\n" +
                                    "\t    \"groupImageUrl\": \"https://example.com/group1.png\",\n" +
                                    "\t    \"participant\": 10,\n" +
                                    "\t    \"isPin\" : true\n" +
                                    "\t  },\n" +
                                    "\t  {\n" +
                                    "\t    \"groupId\": 2,\n" +
                                    "\t    \"groupName\": \"Workout Group\",\n" +
                                    "\t    \"groupImageUrl\": \"https://example.com/group2.png\",\n" +
                                    "\t    \"participant\": 15,\n" +
                                    "\t    \"isPin\" : true\n" +
                                    "\t  },\n" +
                                    "\t  {\n" +
                                    "\t    \"groupId\": 3,\n" +
                                    "\t    \"groupName\": \"Gaming Group\",\n" +
                                    "\t    \"groupImageUrl\": \"https://example.com/group3.png\",\n" +
                                    "\t    \"participant\": 8,\n" +
                                    "\t    \"isPin\" : false\n" +
                                    "\t\t}\n" +
                                    "  ]\n" +
                                    "]")))
    })
    ResponseEntity<DataResponse<List<GroupResponseDTO>>> groupList(
            @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "초대 조회", description = "초대 받은 목록을 보여줍니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "초대 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\n" +
                                    "\t\"data\" : [\n" +
                                    "\t\t{\n" +
                                    "\t    \"groupId\": 1,\n" +
                                    "\t    \"groupName\": \"Study Group\",\n" +
                                    "\t    \"groupImageUrl\": \"https://example.com/group1.png\",\n" +
                                    "\t  },\n" +
                                    "\t  {\n" +
                                    "\t    \"groupId\": 2,\n" +
                                    "\t    \"groupName\": \"Workout Group\",\n" +
                                    "\t    \"groupImageUrl\": \"https://example.com/group2.png\",\n" +
                                    "\t  },\n" +
                                    "\t  {\n" +
                                    "\t    \"groupId\": 3,\n" +
                                    "\t    \"groupName\": \"Gaming Group\",\n" +
                                    "\t    \"groupImageUrl\": \"https://example.com/group3.png\",\n" +
                                    "\t\t}\n" +
                                    "\t]\n" +
                                    "}")))
    })
    ResponseEntity<DataResponse<List<GroupResponseDTO>>> groupInviteList(
            @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "그룹 탈퇴", description = "해당 그룹에서 탈퇴합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "그룹 탈퇴 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\n" +
                                    "\t\"resultCode\": 200,\n" +
                                    "  \"resultMsg\": \"그룹 탈퇴 성공\"\n" +
                                    "}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "해당 그룹에서 유저를 찾을 수 없는 경우",
                                            value = "{\n" +
                                                    "\t\"resultCode\": 400,\n" +
                                                    "  \"resultMsg\": \"이미 그룹에 속하지 않습니다.\"\n" +
                                                    "}"),
                                    @ExampleObject(name = "그룹 초대메시지를 수락/거절 하지 않은 경우",
                                            value = "{\n" +
                                                    "\t\"resultCode\": 400,\n" +
                                                    "  \"resultMsg\": \"초대 수락/거절을 먼저 수행하십시오.\"\n" +
                                                    "}"),
                                    @ExampleObject(name = "해당 그룹에서 리더인 경우",
                                            value = "{\n" +
                                                    "\t\"resultCode\": 400,\n" +
                                                    "  \"resultMsg\": \"그룹 리더는 그룹을 떠날 수 없습니다.\"\n" +
                                                    "}")
                            })
            )
    })
    ResponseEntity<BaseResponse> leaveGroup(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable("groupId") Long groupId);

    @Operation(summary = "그룹 삭제", description = "해당 그룹을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "그룹 삭제 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\n" +
                                    "\t\"resultCode\": 200,\n" +
                                    "  \"resultMsg\": \"그룹 삭제 성공\"\n" +
                                    "}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "해당 그룹이 이미 없는 경우",
                                            value = "{\n" +
                                                    "\t\"resultCode\": 400,\n" +
                                                    "  \"resultMsg\": \"해당 그룹이 존재하지 않습니다.\"\n" +
                                                    "}"),
                                    @ExampleObject(name = "해당 그룹의 리더가 아닌경우",
                                            value = "{\n" +
                                                    "\t\"resultCode\": 400,\n" +
                                                    "  \"resultMsg\": \"그룹 삭제 권한이 없습니다.\"\n" +
                                                    "}")
                            })
            )
    })
    ResponseEntity<BaseResponse> deleteGroup(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable("groupId") Long groupId);

    @Operation(summary = "초대 거절", description = "초대를 거절합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "초대 거절 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\n" +
                                    "\t\"resultCode\": 200,\n" +
                                    "  \"resultMsg\": \"초대 거절 성공\"\n" +
                                    "}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "그룹을 찾을 수 없는 경우.",
                                            value = "{\n" +
                                                    "  \"ResultCode\": 400,\n" +
                                                    "  \"ResultMsg\": \"해당 그룹이 존재하지 않습니다.\"\n" +
                                                    "}"),
                                    @ExampleObject(name = "초대 받지 않은경우.",
                                            value = "{\n" +
                                                    "  \"ResultCode\": 400,\n" +
                                                    "  \"ResultMsg\": \"초대 받지 않았습니다.\"\n" +
                                                    "}"),
                                    @ExampleObject(name = "이미 그룹에 있는 경우.",
                                            value = "{\n" +
                                                    "  \"ResultCode\": 400,\n" +
                                                    "  \"ResultMsg\": \"이미 그룹에 속해 있습니다.\"\n" +
                                                    "}")
                            })
            )
    })
    ResponseEntity<BaseResponse> declineInvitation(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @PathVariable("groupId") Long groupId);

    @Operation(summary = "강제 탈퇴", description = "그룹리더가 그룹원을 강제 탈퇴 시킵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "강제 탈퇴 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\n" +
                                    "\t\"resultCode\": 200,\n" +
                                    "  \"resultMsg\": \"강제 퇴출 성공\"\n" +
                                    "}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "해당 그룹의 리더가 아닌 경우.",
                                            value = "{\n" +
                                                    "  \"ResultCode\": 400,\n" +
                                                    "  \"ResultMsg\": \"강제 퇴출시킬 권한이 없습니다.\"\n" +
                                                    "}"),
                                    @ExampleObject(name = "해당 그룹에 속해 있지 않은멤버를 퇴출시키는 경우",
                                            value = "{\n" +
                                                    "  \"ResultCode\": 400,\n" +
                                                    "  \"ResultMsg\": \"그룹에 속해 있지 않은 멤버입니다.\"\n" +
                                                    "}"),
                                    @ExampleObject(name = "리더가 자신을 퇴출시키려 하는 경우.",
                                            value = "{\n" +
                                                    "  \"ResultCode\": 400,\n" +
                                                    "  \"ResultMsg\": \"자기자신을 퇴출시킬 수 없습니다.\"\n" +
                                                    "}")
                            })
            )
    })
    ResponseEntity<BaseResponse> forceExpelMember(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @PathVariable("groupId") Long groupId,
                                                  @PathVariable("username") String username);

    @Operation(summary = "그룹 상단 고정/해제", description = "해당 그룹을 상단 고정/해제 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상단 고정/해제 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\n" +
                                    "\t\"resultCode\": 200,\n" +
                                    "  \"resultMsg\": \"상단 고정/해제 성공\"\n" +
                                    "}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "해당 그룹이 없는 경우",
                                            value = "{\n" +
                                                    "\t\"resultCode\": 400,\n" +
                                                    "  \"resultMsg\": \"해당 그룹이 존재하지 않습니다.\"\n" +
                                                    "}"),
                                    @ExampleObject(name = "해당 그룹의 그룹원이 아닌경우",
                                            value = "{\n" +
                                                    "\t\"resultCode\": 400,\n" +
                                                    "  \"resultMsg\": \"해당 그룹의 그룹원이 아닙니다.\"\n" +
                                                    "}")
                            })
            )
    })
    ResponseEntity<BaseResponse> pinedGroup(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable("groupId") Long groupId);

    @Operation(summary = "그룹 멤버 조회", description = "그룹 멤버를 조회합니다.")
    @ApiResponses
            ({
                    @ApiResponse
                            (
                                    responseCode = "200",
                                    description = "그룹 멤버 조회 성공",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "members": [
                                                                                    {
                                                                                        "name": "이수현",
                                                                                        "username": "shuding0307",
                                                                                        "groupRole": "LEADER",
                                                                                        "friendStatus": "FRIEND",
                                                                                        "profileImage": "https://example.com/profile/suhyeon.jpg"
                                                                                    },
                                                                                    {
                                                                                        "name": "이상준",
                                                                                        "username": "sang__00",
                                                                                        "groupRole": "PARTICIPANT",
                                                                                        "friendStatus": "NONE",
                                                                                        "profileImage": "https://example.com/profile/sangjoon.jpg"
                                                                                    },
                                                                                    {
                                                                                        "name": "김도하",
                                                                                        "username": "ehgk4245",
                                                                                        "groupRole": "PARTICIPANT",
                                                                                        "friendStatus": "FRIEND",
                                                                                        "profileImage": "https://example.com/profile/doha.jpg"
                                                                                    },
                                                                                    {
                                                                                        "name": "최준혁",
                                                                                        "username": "_twinkle_high",
                                                                                        "groupRole": "PARTICIPANT",
                                                                                        "friendStatus": "RECEIVE",
                                                                                        "profileImage": "https://example.com/profile/junhyeok.jpg"
                                                                                    },
                                                                                    {
                                                                                        "name": "이다은",
                                                                                        "username": "Euniii0713",
                                                                                        "groupRole": "PARTICIPANT",
                                                                                        "friendStatus": "ME",
                                                                                        "profileImage": "https://example.com/profile/daeun.jpg"
                                                                                    }
                                                                                ]
                                                                            }
                                                                            """
                                                            )
                                            )
                            ),
                    @ApiResponse
                            (
                                    responseCode = "404",
                                    description = "그룹을 찾을 수 없는 경우",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "ResultCode": 404,
                                                                                "ResultMsg": "그룹을 찾을 수 없습니다."
                                                                            }
                                                                            """
                                                            )
                                            )
                            ),
                    @ApiResponse
                            (
                                    responseCode = "403",
                                    description = "그룹원이 아닌 경우",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "ResultCode": 403,
                                                                                "ResultMsg": "접근 권한이 없습니다."
                                                                            }
                                                                            """
                                                            )
                                            )
                            )
            })
    ResponseEntity<GroupMembersResponse> findGroupMembers(@RequestParam("search") @Nullable String keyword,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails,
                                                          @PathVariable("groupId") Long groupId);

    @Operation(summary = "그룹원이 아닌 친구 조회", description = "그룹원이 아닌 친구 목록을 조회합니다.")
    @ApiResponses
            ({
                    @ApiResponse
                            (
                                    responseCode = "200",
                                    description = "그룹원이 아닌 친구 조회 성공",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "nonGroupFriends": [
                                                                                    {
                                                                                        "name": "정재호",
                                                                                        "username": "purify_0kcal",
                                                                                        "profileImage": "https://example.com/profile/suhyeon.jpg",
                                                                                        "status": "PROGRESS"
                                                                                    },
                                                                                    {
                                                                                        "name": "김도하",
                                                                                        "username": "ehgk4245",
                                                                                        "profileImage": "https://example.com/profile/doha.jpg",
                                                                                        "status": "NONE"
                                                                                    }
                                                                                ]
                                                                            }
                                                                            """
                                                            )
                                            )
                            ),
                    @ApiResponse
                            (
                                    responseCode = "404",
                                    description = "그룹을 찾을 수 없는 경우",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "ResultCode": 404,
                                                                                "ResultMsg": "그룹을 찾을 수 없습니다."
                                                                            }
                                                                            """
                                                            )
                                            )
                            ),
                    @ApiResponse
                            (
                                    responseCode = "403",
                                    description = "그룹원이 아닌 경우",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "ResultCode": 403,
                                                                                "ResultMsg": "접근 권한이 없습니다."
                                                                            }
                                                                            """
                                                            )
                                            )
                            )
            })
    ResponseEntity<NonGroupFriendsResponse> findNonGroupFriends(@RequestParam("search") @Nullable String keyword,
                                                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                                                @PathVariable("groupId") Long groupId);

    @Operation(summary = "등록한 가능한 날짜의 비율", description = "그룹원이 등록한 가능한 날짜의 비율을 제공합니다.")
    @ApiResponses
            ({
                    @ApiResponse
                            (
                                    responseCode = "200",
                                    description = "등록한 가능한 날짜의 비율 조회 성공",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "availableDateRatios": [
                                                                                    {
                                                                                        "date": "2025-01-08",
                                                                                        "ratio": 15.3333
                                                                                    },
                                                                                    {
                                                                                        "date": "2025-01-11",
                                                                                        "ratio": 27
                                                                                    },
                                                                                    {
                                                                                        "date": "2025-01-19",
                                                                                        "ratio": 66.666
                                                                                    }
                                                                                ]
                                                                            }
                                                                            """
                                                            )

                                            )
                            ),
                    @ApiResponse
                            (
                                    responseCode = "404",
                                    description = "그룹을 찾을 수 없는 경우",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "ResultCode": 404,
                                                                                "ResultMsg": "그룹을 찾을 수 없습니다."
                                                                            }
                                                                            """
                                                            )
                                            )
                            ),
                    @ApiResponse
                            (
                                    responseCode = "403",
                                    description = "그룹원이 아닌 경우",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "ResultCode": 403,
                                                                                "ResultMsg": "접근 권한이 없습니다."
                                                                            }
                                                                            """
                                                            )
                                            )
                            )
            })
    ResponseEntity<AvailableDateRatios> findAvailableDateRatios(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                @PathVariable("groupId") Long groupId,
                                                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth);

    @Operation(summary = "가능한 날짜를 등록한 멤버 조회", description = "특정 날짜에 가능한 날짜를 등록한 멤버들을 조회합니다.")
    @ApiResponses
            ({
                    @ApiResponse
                            (
                                    responseCode = "200",
                                    description = "가능한 날짜를 등록한 멤버 조회 성공",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "availableMembers": [
                                                                                    "이수현",
                                                                                    "최준혁",
                                                                                    "정재호"
                                                                                ]
                                                                            }
                                                                            """
                                                            )
                                            )
                            ),
                    @ApiResponse
                            (
                                    responseCode = "404",
                                    description = "그룹을 찾을 수 없는 경우",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "ResultCode": 404,
                                                                                "ResultMsg": "그룹을 찾을 수 없습니다."
                                                                            }
                                                                            """
                                                            )
                                            )
                            ),
                    @ApiResponse
                            (
                                    responseCode = "403",
                                    description = "그룹원이 아닌 경우",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "ResultCode": 403,
                                                                                "ResultMsg": "접근 권한이 없습니다."
                                                                            }
                                                                            """
                                                            )
                                            )
                            )
            })
    ResponseEntity<Map<String, Object>> getAvailableMembers(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @PathVariable("groupId") Long groupId,
                                                            @RequestParam @DateTimeFormat(pattern = "yyyy-mm-dd") @Nullable LocalDate date);

    @Operation(summary = "그룹 멤버의 가능한 날짜 조회", description = "그룹 멤버들이 등록한 가능한 날짜들을 조회합니다")
    @ApiResponses
            ({
                    @ApiResponse
                            (
                                    responseCode = "200",
                                    description = "그룹 멤버의 가능한 날짜 조회 성공",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "availableMemberInfos": [
                                                                                    {
                                                                                        "memberName": "정재호",
                                                                                        "profileImage": "example.com/jjaeho",
                                                                                        "availableDates": [
                                                                                            "2025-01-22",
                                                                                            "2025-01-23",
                                                                                            "2025-01-24"
                                                                                        ]
                                                                                    },
                                                                                    {
                                                                                        "memberName": "최준혁",
                                                                                        "profileImage": "example.com/kkk",
                                                                                        "availableDates": []
                                                                                    }
                                                                                ]
                                                                            }
                                                                            """
                                                            )
                                            )
                            ),
                    @ApiResponse
                            (
                                    responseCode = "404",
                                    description = "그룹을 찾을 수 없는 경우",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "ResultCode": 404,
                                                                                "ResultMsg": "그룹을 찾을 수 없습니다."
                                                                            }
                                                                            """
                                                            )
                                            )
                            ),
                    @ApiResponse
                            (
                                    responseCode = "403",
                                    description = "그룹원이 아닌 경우",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "ResultCode": 403,
                                                                                "ResultMsg": "접근 권한이 없습니다."
                                                                            }
                                                                            """
                                                            )
                                            )
                            )
            })
    ResponseEntity<AvailableMemberInfos> getAvailableMemberInfos(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                 @PathVariable("groupId") Long groupId,
                                                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM") @Nullable YearMonth yearMonth);

    @Operation(summary = "가능한 날짜를 등록한 멤버 이름 조회", description = "요청한 달의 가능한 날짜를 등록한 멤버 이름을 조회합니다.")
    @ApiResponses
            ({
                    @ApiResponse
                            (
                                    responseCode = "200",
                                    description = "가능한 날짜를 등록한 멤버 이름을 조회합니다.",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "availableDateInfos": [
                                                                                    {
                                                                                        "availableDate": "2024-01-20",
                                                                                        "memberNames": [
                                                                                            "최준혁",
                                                                                            "정재호",
                                                                                            "김도하"
                                                                                        ]
                                                                                    },
                                                                                    {
                                                                                        "availableDate": "2024-01-21",
                                                                                        "memberNames": [
                                                                                            "이수현",
                                                                                            "이다은",
                                                                                            "이상준"
                                                                                        ]
                                                                                    }
                                                                                ]
                                                                            }
                                                                            """
                                                            )
                                            )
                            ),
                    @ApiResponse
                            (
                                    responseCode = "404",
                                    description = "그룹을 찾을 수 없는 경우",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "ResultCode": 404,
                                                                                "ResultMsg": "그룹을 찾을 수 없습니다."
                                                                            }
                                                                            """
                                                            )
                                            )
                            ),
                    @ApiResponse
                            (
                                    responseCode = "403",
                                    description = "그룹원이 아닌 경우",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "ResultCode": 403,
                                                                                "ResultMsg": "접근 권한이 없습니다."
                                                                            }
                                                                            """
                                                            )
                                            )
                            )
            })
    ResponseEntity<AvailableDateInfos> getAvailableDateInfos(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                             @PathVariable("groupId") Long groupId,
                                                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM") @Nullable YearMonth yearMonth);

    @Operation(summary = "가능한 날짜 순위별 조회", description = "멤버들이 등록한 가능한 날짜 중 순위별로 조회합니다.")
    @ApiResponses
            ({
                    @ApiResponse
                            (
                                    responseCode = "200",
                                    description = "가능한 날짜 순위별 조회 성공",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "availableDateRanks": [
                                                                                    {
                                                                                        "ranks": 1,
                                                                                        "date": "2025-02-12"
                                                                                    },
                                                                                    {
                                                                                        "ranks": 2,
                                                                                        "date": "2025-02-18"
                                                                                    },
                                                                                    {
                                                                                        "ranks": 2,
                                                                                        "date": "2025-02-22"
                                                                                    },
                                                                                    {
                                                                                        "ranks": 3,
                                                                                        "date": "2025-02-24"
                                                                                    }
                                                                                ]
                                                                            }
                                                                            """
                                                            )
                                            )
                            ),
                    @ApiResponse
                            (
                                    responseCode = "404",
                                    description = "그룹을 찾을 수 없는 경우",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "ResultCode": 404,
                                                                                "ResultMsg": "그룹을 찾을 수 없습니다."
                                                                            }
                                                                            """
                                                            )
                                            )
                            ),
                    @ApiResponse
                            (
                                    responseCode = "403",
                                    description = "그룹원이 아닌 경우",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "ResultCode": 403,
                                                                                "ResultMsg": "접근 권한이 없습니다."
                                                                            }
                                                                            """
                                                            )
                                            )
                            )
            })
    ResponseEntity<AvailableDateRanks> getAvailableDateRanks(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("groupId") Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") @Nullable YearMonth yearMonth);

    @Operation(summary = "그룹 상세 조회", description = "그룹 상세를 조회합니다.")
    @ApiResponses
            ({
                    @ApiResponse
                            (
                                    responseCode = "200",
                                    description = "그룹 상세 조회 성공",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                example = """
                                                                        {
                                                                            "groupInfo": {
                                                                                "groupName": "춘천팟",
                                                                                "isPin": false
                                                                            }
                                                                        }
                                                                        """
                                                            )
                                            )
                            ),
                    @ApiResponse
                            (
                                    responseCode = "404",
                                    description = "그룹을 찾을 수 없는 경우",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "ResultCode": 404,
                                                                                "ResultMsg": "그룹을 찾을 수 없습니다."
                                                                            }
                                                                            """
                                                            )
                                            )
                            ),
                    @ApiResponse
                            (
                                    responseCode = "403",
                                    description = "그룹원이 아닌 경우",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "ResultCode": 403,
                                                                                "ResultMsg": "접근 권한이 없습니다."
                                                                            }
                                                                            """
                                                            )
                                            )
                            )
            })
    ResponseEntity<Map<String, Object>> getGroupDetails(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                        @PathVariable("groupId") Long groupId);
}
