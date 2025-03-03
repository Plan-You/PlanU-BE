package com.planu.group_meeting.controller.docs;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.AvailableDateDto.AvailableDateRanks;
import com.planu.group_meeting.dto.AvailableDateDto.AvailableDateRatios;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.dto.GroupDTO.AvailableDateInfos;
import com.planu.group_meeting.dto.GroupDTO.AvailableMemberInfos;
import com.planu.group_meeting.dto.GroupDTO.GroupMembersResponse;
import com.planu.group_meeting.dto.GroupDTO.NonGroupFriendsResponse;
import com.planu.group_meeting.dto.GroupInviteResponseDTO;
import com.planu.group_meeting.dto.GroupResponseDTO;
import com.planu.group_meeting.dto.DataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "GROUP API", description = "그룹 API")
public interface GroupDocs {

    ResponseEntity<GroupResponseDTO> createGroup(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                 @RequestParam("groupName") String groupName,
                                                 @RequestParam("groupImage") MultipartFile groupImage);

    ResponseEntity<GroupInviteResponseDTO> inviteUser(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                      @RequestParam("groupId") Long id,
                                                      @RequestParam("username") String userName);

    ResponseEntity<BaseResponse> inviteUserCancel(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @RequestParam("groupId") Long groupId,
                                                  @RequestParam("username") String username);

    ResponseEntity<BaseResponse> joinGroup(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @PathVariable("groupId") Long groupId);

    ResponseEntity<DataResponse<List<GroupResponseDTO>>> groupList(
            @AuthenticationPrincipal CustomUserDetails userDetails);

    ResponseEntity<DataResponse<List<GroupResponseDTO>>> groupInviteList(
            @AuthenticationPrincipal CustomUserDetails userDetails);

    ResponseEntity<BaseResponse> leaveGroup(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable("groupId") Long groupId);

    ResponseEntity<BaseResponse> deleteGroup(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable("groupId") Long groupId);

    ResponseEntity<BaseResponse> declineInvitation(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @PathVariable("groupId") Long groupId);

    ResponseEntity<BaseResponse> forceExpelMember(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @PathVariable("groupId") Long groupId,
                                                  @PathVariable("username") String username);

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
    ResponseEntity<List<AvailableDateRanks>> getAvailableDateRanks(
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
