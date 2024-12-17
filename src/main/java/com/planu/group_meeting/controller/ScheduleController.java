package com.planu.group_meeting.controller;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.dto.ScheduleDto;
import com.planu.group_meeting.dto.ScheduleDto.DailyScheduleResponse;
import com.planu.group_meeting.dto.ScheduleDto.ScheduleCheckResponse;
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
import java.util.List;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<BaseResponse> createSchedules(@Valid @RequestBody ScheduleSaveRequest scheduleDto,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        scheduleService.createSchedule(userDetails.getId(), scheduleDto);
        return BaseResponse.toResponseEntity(HttpStatus.CREATED,"일정 생성 성공");
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleDetailsResponse> getScheduleDetails(@PathVariable("scheduleId") Long scheduleId) {
        ScheduleDetailsResponse response = scheduleService.findScheduleDetails(scheduleId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<DailyScheduleResponse> getScheduleList(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(scheduleService.findScheduleList(userDetails.getId(), startDate, endDate));
    }

    @GetMapping("/check-events")
    public ResponseEntity<List<ScheduleCheckResponse>>checkScheduleList(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth requestDate,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(scheduleService.getSchedulesForMonth(userDetails.getId(), requestDate));
    }

}
