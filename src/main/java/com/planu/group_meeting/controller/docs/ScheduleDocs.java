package com.planu.group_meeting.controller.docs;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.dto.ScheduleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Tag(name = "SCHEDULE API", description = "개인 스케줄 관련 API")
public interface ScheduleDocs {

    @Operation(summary = "개인일정 생성", description = "개인 일정을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "일정 생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 201, \"resultMsg\": \"일정 생성 성공\" }"))),
            @ApiResponse(responseCode = "400", description = "일정 제목, 메모 유효성 검증 오류",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "일정 제목 누락",
                                            value = "{ \"resultCode\": 400, \"resultMsg\": \"일정 제목은 필수 입력 값입니다.\" }"),
                                    @ExampleObject(name = "일정 제목 20자 초과",
                                            value = "{ \"resultCode\": 400, \"resultMsg\": \"일정 제목은 20자 이내로 입력해주세요.\" }"),
                                    @ExampleObject(name = "메모 100자 초과",
                                            value = "{ \"resultCode\": 400, \"resultMsg\": \"메모는 100자 이내로 입력해주세요.\" }")
                            })
            )
    })
    ResponseEntity<BaseResponse> createSchedules(@Valid @RequestBody ScheduleDto.ScheduleSaveRequest scheduleSaveRequest,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "개인일정 수정", description = "개인 일정을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일정 수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 200, \"resultMsg\": \"일정 수정 성공\" }"))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 일정 아이디",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 404, \"resultMsg\": \"scheduleId 1 : 해당하는 스케줄을 찾을 수 없습니다.\" }"))),
            @ApiResponse(responseCode = "403", description = "사용자가 생성한 일정이 아닌 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 403, \"resultMsg\": \"변경 권한이 없습니다.\" }"))),
            @ApiResponse(responseCode = "400", description = "일정 제목, 메모 유효성 검증 오류",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "일정 제목 누락",
                                            value = "{ \"resultCode\": 400, \"resultMsg\": \"일정 제목은 필수 입력 값입니다.\" }"),
                                    @ExampleObject(name = "일정 제목 20자 초과",
                                            value = "{ \"resultCode\": 400, \"resultMsg\": \"일정 제목은 20자 이내로 입력해주세요.\" }"),
                                    @ExampleObject(name = "메모 100자 초과",
                                            value = "{ \"resultCode\": 400, \"resultMsg\": \"메모는 100자 이내로 입력해주세요.\" }")
                            })

            )
    })
    ResponseEntity<BaseResponse> updateSchedule(@PathVariable("scheduleId") Long scheduleId,
                                                @Valid @RequestBody ScheduleDto.ScheduleSaveRequest scheduleSaveRequest,
                                                @AuthenticationPrincipal CustomUserDetails userDetails);


    @Operation(summary = "개인일정 삭제", description = "개인일정을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "일정 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"resultCode\": 200,\n  \"resultMsg\": \"일정 삭제 성공\"\n}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "일정이 존재하지 않을 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"resultCode\": 404,\n  \"resultMsg\": \"scheduleId 3 : 해당하는 스케줄을 찾을 수 없습니다.\"\n}")
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자가 생성한 일정이 아닌 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"resultCode\": 403,\n  \"resultMsg\": \"변경 권한이 없습니다.\"\n}")
                    )
            )
    })
    ResponseEntity<BaseResponse> deleteSchedule(@PathVariable("scheduleId") Long scheduleId,
                                                @AuthenticationPrincipal CustomUserDetails userDetails);


    @Operation(summary = "개인일정 상세 조회", description = "개인일정의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "스케줄 상세 정보 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\n  \"id\": 1,\n  \"title\": \"회의 일정\",\n  \"startDateTime\": \"2024-11-21T09:00:00\",\n  " +
                                            "\"endDateTime\": \"2024-11-21T10:00:00\",\n  \"color\": \"#FFA500\",\n  \"location\": \"신촌역 5번 출구\",\n  " +
                                            "\"memo\": \"팀 회의\",\n  \"participants\": [\n    { \"id\": 101, \"name\": \"김도하\" },\n    { \"id\": 102, \"name\": \"최준혁\" }\n  ],\n  " +
                                            "\"unregisteredParticipants\": [\n    { \"name\": \"아무개1\" },\n    { \"name\": \"아무개2\" }\n  ]\n}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "일정이 존재하지 않을 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"resultCode\": 404,\n  \"resultMsg\": \"scheduleId 3 : 해당하는 스케줄을 찾을 수 없습니다.\"\n}")
                    )
            )
    })
    ResponseEntity<ScheduleDto.ScheduleDetailsResponse> getScheduleDetails(@PathVariable("scheduleId") Long scheduleId);


    @Operation(summary = "일정 목록 조회", description = "일정 목록을 조회합니다. 시작 날짜와 종료 날짜를 기준으로 필터링할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "일정 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\n  \"schedules\": [\n    {\n      \"id\": 1,\n      \"groupId\": 1,\n      \"title\": \"Weekly Meeting\",\n      \"location\": \"Library Room A\",\n      \"latitude\": \"37.5665\",\n      \"longitude\": \"126.9780\",\n      \"startTime\": \"10:00\",\n      \"endTime\": \"12:00\",\n      \"color\": \"#FF5733\"\n    },\n    {\n      \"id\": 1,\n      \"groupId\": null,\n      \"title\": \"회의 일정\",\n      \"location\": \"강원 춘천시 백령로 51\",\n      \"latitude\": \"37.5245\",\n      \"longitude\": \"17.1545\",\n      \"startTime\": \"10:00\",\n      \"endTime\": \"12:59\",\n      \"color\": \"#FFA500\"\n    },\n    {\n      \"id\": 2,\n      \"groupId\": null,\n      \"title\": \"프로젝트 회의\",\n      \"location\": \"강원 춘천시 백령로 51\",\n      \"latitude\": \"171.135\",\n      \"longitude\": \"153.5165\",\n      \"startTime\": \"13:00\",\n      \"endTime\": \"15:59\",\n      \"color\": \"#FFA500\"\n    },\n    {\n      \"id\": 2,\n      \"groupId\": 1,\n      \"title\": \"Project Discussion\",\n      \"location\": \"Conference Room B\",\n      \"latitude\": \"37.1234\",\n      \"longitude\": \"127.5678\",\n      \"startTime\": \"14:00\",\n      \"endTime\": \"15:30\",\n      \"color\": \"#33FF57\"\n    }\n  ],\n  \"birthdayFriends\": [\n    {\n      \"date\": \"09-11\",\n      \"names\": \"최준혁, 김도하\"\n    }\n  ]\n}")
                    )
            )
    })
    ResponseEntity<ScheduleDto.DailyScheduleResponse> getScheduleList(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @AuthenticationPrincipal CustomUserDetails userDetails);


    @Operation(summary = "특정 달의 일정 확인", description = "요청한 달의 달력에 이벤트(개인일정, 그룹일정, 생일인 친구)가 있는지 확인합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "일정 확인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "[\n    {\n        \"date\": \"2024-01-28\",\n        \"scheduleTypes\": [\n            \"personal\",\n            \"group\",\n            \"birthday\"\n        ]\n    },\n    {\n        \"date\": \"2024-02-05\",\n        \"scheduleTypes\": [\n            \"personal\"\n        ]\n    }\n]")
                    )
            )
    })
    ResponseEntity<List<ScheduleDto.ScheduleCheckResponse>> checkScheduleList(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth requestDate,
            @AuthenticationPrincipal CustomUserDetails userDetails);


}
