package com.planu.group_meeting.controller.docs;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.dto.GroupScheduleDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

@Tag(name = "GROUP SCHEDULE API", description = "그룹 일정 API")
public interface GroupScheduleDocs {

    @Operation(summary = "그룹 일정 생성", description = "그룹 일정을 생성합니다.")
    @ApiResponses(
            {
                    @ApiResponse
                            (
                                    responseCode = "201",
                                    description = "일정 생성 성공",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = "{\"resultCode\": 201, \"resultMsg\": \"일정 생성 성공\"}"
                                                            )
                                            )
                            ),
                    @ApiResponse
                            (
                                    responseCode = "400",
                                    description = "일정 제목, 메모 유효성 검사 오류",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    examples =
                                                            {
                                                                    @ExampleObject
                                                                            (
                                                                                    name = "일정 제목 누락",
                                                                                    value = "{\"resultCode\": 400, \"resultMsg\": \"일정 제목은 필수 입력 값입니다.\"}"
                                                                            ),
                                                                    @ExampleObject
                                                                            (
                                                                                    name = "일정 제목 20자 초과",
                                                                                    value = "{\"resultCode\": 400, \"resultMsg\": \"일정 제목은 20자 이내로 입력해주세요.\"}"
                                                                            ),
                                                                    @ExampleObject
                                                                            (
                                                                                    name = "메모 100자 초과",
                                                                                    value = "{\"resultCode\": 400, \"resultMsg\": \"메모는 100자 이내로 입력해주세여.\"}"
                                                                            )

                                                            }
                                            )
                            ),
                    @ApiResponse
                            (
                                    responseCode = "404",
                                    description = "그룹을 찾을 수 없습니다.",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = "{\"resultCode\": 404, \"resultMsg\": \"그룹을 찾을 수 없습니다.\"}"
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
                                                                    example = "{\"resultCode\": 403, \"resultMsg\": \"접근 권한이 없습니다.\"}"
                                                            )
                                            )
                            )
            }
    )
    public ResponseEntity<BaseResponse> create(@Valid @RequestBody GroupScheduleDTO.GroupScheduleRequest groupScheduleRequest,
                                               @PathVariable Long groupId,
                                               @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "오늘 일정 정보", description = "오늘 일정 정보를 조회합니다.")
    @ApiResponses
            ({
                    @ApiResponse
                            (
                                    responseCode = "200",
                                    description = "오늘 일정 정보 조회 성공",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "groupName": "춘천팟",
                                                                                "todaySchedules": [
                                                                                    {
                                                                                        "id": 3,
                                                                                        "title": "수현이 생일파티",
                                                                                        "startDateTime": "오후 7시",
                                                                                        "location": "홍대입구역 2번 출구"
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
                                    description = "그룹을 찾을 수 없습니다.",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "resultCode": 404,
                                                                                "resultMsg": "그룹을 찾을 수 없습니다."
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
                                                                                "resultCode": 403,
                                                                                "resultMsg": "접근 권한이 없습니다."
                                                                            }
                                                                            """
                                                            )
                                            )
                            )
            })
    public ResponseEntity<GroupScheduleDTO.GroupTodayScheduleResponse> groupTodaySchedule(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                          @PathVariable("groupId") Long groupId);

    @Operation(summary = "달력의 그룹 정보", description = "그룹 달력에 그룹 일정 정보를 제공")
    @ApiResponses
            ({
                    @ApiResponse
                            (
                                    responseCode = "200",
                                    description = "그룹 달력 일정 조회 성공",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "groupSchedules": [
                                                                                    {
                                                                                        "id": 5,
                                                                                        "title": "술약",
                                                                                        "startDateTime": "2024-02-20",
                                                                                        "endDateTime": "2024-02-20",
                                                                                        "color": "#44AA44"
                                                                                    },
                                                                                    {
                                                                                        "id": 8,
                                                                                        "title": "1박 2일 여행",
                                                                                        "startDateTime": "2024-02-02",
                                                                                        "endDateTime": "2024-02-03",
                                                                                        "color": "#55FFFF"
                                                                                    },
                                                                                    {
                                                                                        "id": 3,
                                                                                        "title": "수현이 생일파티",
                                                                                        "startDateTime": "2024-02-19",
                                                                                        "endDateTime": "2024-02-19",
                                                                                        "color": "#22FFFF"
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
                                    description = "그룹을 찾을 수 없습니다.",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = "{\"resultCode\": 404, \"resultMsg\": \"그룹을 찾을 수 없습니다.\"}"
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
                                                                    example = "{\"resultCode\": 403, \"resultMsg\": \"접근 권한이 없습니다.\"}"
                                                            )
                                            )
                            )

            })
    public ResponseEntity<GroupScheduleDTO.groupOverViewsResponse> groupRequestSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    );

    @Operation(summary = "그룹 일정 상세 조회", description = "그룹 일정 상세를 조회합니다.")
    @ApiResponses
            ({
                    @ApiResponse
                            (
                                    responseCode = "200",
                                    description = "그룹 일정 상세 조회 성공",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "groupScheduleId": 14,
                                                                                "title": "수현이의 생일 파티",
                                                                                "color": "#FFFFFF",
                                                                                "startDate": "2024-02-19T19:00:00",
                                                                                "endDate": "2024-02-19T21:30:00",
                                                                                "location": "홍대입구역 2번 출구 앞",
                                                                                "latitude": 30.12,
                                                                                "longitude": 19.24,
                                                                                "memo": "모두 필참할 것!!!",
                                                                                "participants": [
                                                                                    {
                                                                                        "profileImage": "https://example.com/dlekdms",
                                                                                        "name": "이다은",
                                                                                        "username": "dlekdms"
                                                                                    },
                                                                                    {
                                                                                        "profileImage": "https://example.com/dltngus",
                                                                                        "name": "이수현",
                                                                                        "username": "dltngus"
                                                                                    },
                                                                                    {
                                                                                        "profileImage": "https://example.com/wjdwogh",
                                                                                        "name": "정재호",
                                                                                        "username": "wjdwogh"
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
                                    description = "그룹 또는 일정이 없는 경우",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    examples = {
                                                            @ExampleObject
                                                                    (
                                                                            name = "그룹을 찾을 수 없는 경우",
                                                                            value = """
                                                                                    {
                                                                                        "ResultCode": 404,
                                                                                        "ResultMsg": "그룹을 찾을 수 없습니다."
                                                                                    }
                                                                                    """
                                                                    ),
                                                            @ExampleObject
                                                                    (
                                                                            name = "그룹 일정을 찾을 수 없는 경우",
                                                                            value = """
                                                                                    {
                                                                                        "ResultCode": 404,
                                                                                        "ResultMsg": "해당 그룹 일정을 찾을 수 없습니다."
                                                                                    }
                                                                                    """
                                                                    )
                                                    }
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
    public ResponseEntity<GroupScheduleDTO.GroupSchedulesDetailResponse> groupScheduleDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("groupId") Long groupId,
            @PathVariable("scheduleId") Long scheduleId
    );

    @Operation(summary = "그룹 일정 삭제 API", description = "그룹 일정을 삭제합니다.")
    @ApiResponses
            ({
                    @ApiResponse
                            (
                                    responseCode = "200",
                                    description = "그룹 일정 삭제 성공",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example =
                                                                            """
                                                                                    {
                                                                                        "resultCode": 200,
                                                                                        "resultMsg": "그룹 일정 삭제 성공"
                                                                                    }
                                                                                    """
                                                            )
                                            )
                            ),
                    @ApiResponse
                            (
                                    responseCode = "404",
                                    description = "그룹 또는 일정이 없는 경우",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    examples = {
                                                            @ExampleObject
                                                                    (
                                                                            name = "그룹을 찾을 수 없는 경우",
                                                                            value = """
                                                                                    {
                                                                                        "ResultCode": 404,
                                                                                        "ResultMsg": "그룹을 찾을 수 없습니다."
                                                                                    }
                                                                                    """
                                                                    ),
                                                            @ExampleObject
                                                                    (
                                                                            name = "그룹 일정을 찾을 수 없는 경우",
                                                                            value = """
                                                                                    {
                                                                                        "ResultCode": 404,
                                                                                        "ResultMsg": "해당 그룹 일정을 찾을 수 없습니다."
                                                                                    }
                                                                                    """
                                                                    )
                                                    }
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
    public ResponseEntity<BaseResponse> deleteGroupSchedule(
            @PathVariable("groupId") Long groupId,
            @PathVariable("scheduleId") Long scheduleId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(summary = "그룹 일정 수정 API", description = "그룹 일정을 수정합니다.")
    @ApiResponses
            ({
                    @ApiResponse
                            (
                                    responseCode = "200",
                                    description = "그룹 일정 수정 성공",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example =
                                                                            """
                                                                                    {
                                                                                        "resultCode": 200,
                                                                                        "resultMsg": "그룹 일정 수정 성공"
                                                                                    }
                                                                                    """
                                                            )

                                            )
                            ),
                    @ApiResponse
                            (
                                    responseCode = "400",
                                    description = "일정 제목, 메모 유효성 검사 오류",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    examples =
                                                            {
                                                                    @ExampleObject
                                                                            (
                                                                                    name = "일정 제목 누락",
                                                                                    value = "{\"resultCode\": 400, \"resultMsg\": \"일정 제목은 필수 입력 값입니다.\"}"
                                                                            ),
                                                                    @ExampleObject
                                                                            (
                                                                                    name = "일정 제목 20자 초과",
                                                                                    value = "{\"resultCode\": 400, \"resultMsg\": \"일정 제목은 20자 이내로 입력해주세요.\"}"
                                                                            ),
                                                                    @ExampleObject
                                                                            (
                                                                                    name = "메모 100자 초과",
                                                                                    value = "{\"resultCode\": 400, \"resultMsg\": \"메모는 100자 이내로 입력해주세여.\"}"
                                                                            )

                                                            }
                                            )
                            ),
                    @ApiResponse
                            (
                                    responseCode = "404",
                                    description = "그룹 또는 일정이 없는 경우",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    examples = {
                                                            @ExampleObject
                                                                    (
                                                                            name = "그룹을 찾을 수 없는 경우",
                                                                            value = """
                                                                                    {
                                                                                        "ResultCode": 404,
                                                                                        "ResultMsg": "그룹을 찾을 수 없습니다."
                                                                                    }
                                                                                    """
                                                                    ),
                                                            @ExampleObject
                                                                    (
                                                                            name = "그룹 일정을 찾을 수 없는 경우",
                                                                            value = """
                                                                                    {
                                                                                        "ResultCode": 404,
                                                                                        "ResultMsg": "해당 그룹 일정을 찾을 수 없습니다."
                                                                                    }
                                                                                    """
                                                                    )
                                                    }
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
    public ResponseEntity<BaseResponse> updateGroupSchedule(
            @PathVariable("groupId") Long groupId,
            @PathVariable("scheduleId") Long scheduleId,
            @Valid @RequestBody GroupScheduleDTO.GroupScheduleRequest groupScheduleRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(summary = "그룹 일정 유무 조회 API", description = "그룹 일정 유무를 조회합니다.")
    @ApiResponses
            ({
                    @ApiResponse
                            (
                                    responseCode = "200",
                                    description = "그룹 일정 유무 조회 성공",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "groupScheduleData": [
                                                                                    {
                                                                                        "date": "2025-02-01",
                                                                                        "isSchedule": false,
                                                                                        "isBirthday": true
                                                                                    },
                                                                                    {
                                                                                        "date": "2025-02-06",
                                                                                        "isSchedule": true,
                                                                                        "isBirthday": false
                                                                                    },
                                                                                    {
                                                                                        "date": "2025-02-14",
                                                                                        "isSchedule": true,
                                                                                        "isBirthday": true
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
                                                                                "resultCode": 404,
                                                                                "resultMsg": "그룹을 찾을 수 없습니다."
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
                                                                                "resultCode": 403,
                                                                                "resultMsg": "접근 권한이 없습니다."
                                                                            }
                                                                            """
                                                            )
                                            )
                            )
            })
    public ResponseEntity<Map<String, Object>> getGroupCalendarEvents(
            @PathVariable("groupId") Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") @Nullable YearMonth yearMonth,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
