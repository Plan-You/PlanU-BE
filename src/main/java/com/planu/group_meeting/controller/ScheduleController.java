package com.planu.group_meeting.controller;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.controller.docs.ScheduleDocs;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.dto.ScheduleDto;
import com.planu.group_meeting.dto.ScheduleDto.DailyScheduleResponse;
import com.planu.group_meeting.dto.ScheduleDto.ScheduleDetailsResponse;
import com.planu.group_meeting.dto.ScheduleDto.ScheduleSaveRequest;
import com.planu.group_meeting.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController implements ScheduleDocs {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<BaseResponse> createSchedules(@Valid @RequestBody ScheduleSaveRequest scheduleSaveRequest,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        scheduleService.createSchedule(userDetails.getId(), scheduleSaveRequest);
        return BaseResponse.toResponseEntity(HttpStatus.CREATED, "일정 생성 성공");
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<BaseResponse> updateSchedule(@PathVariable("scheduleId") Long scheduleId,
                                                       @Valid @RequestBody ScheduleSaveRequest scheduleSaveRequest,
                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        scheduleService.updateSchedule(userDetails.getId(), scheduleId, scheduleSaveRequest);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "일정 수정 성공");
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<BaseResponse> deleteSchedule(@PathVariable("scheduleId") Long scheduleId,
                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        scheduleService.deleteSchedule(userDetails.getId(), scheduleId);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "일정 삭제 성공");
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleDetailsResponse> getScheduleDetails(@PathVariable("scheduleId") Long scheduleId) {
        ScheduleDetailsResponse response = scheduleService.findScheduleDetails(scheduleId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list/{username}")
    public ResponseEntity<DailyScheduleResponse> getScheduleList(
            @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(scheduleService.findScheduleList(username, startDate, endDate));
    }

    @GetMapping("/{username}/check-events")
    public ResponseEntity<ScheduleDto.MyScheduleData> checkScheduleList(
            @PathVariable String username, @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(scheduleService.getSchedulesForMonth(userDetails.getId(), username, yearMonth));
    }

}
