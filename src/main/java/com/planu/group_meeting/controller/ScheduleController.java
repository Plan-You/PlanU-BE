package com.planu.group_meeting.controller;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.ScheduleDto;
import com.planu.group_meeting.dto.ScheduleDto.ScheduleDetailsResponse;
import com.planu.group_meeting.dto.ScheduleDto.ScheduleSaveRequest;
import com.planu.group_meeting.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.eclipse.angus.mail.iap.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    @PostMapping
    public ResponseEntity<String>createSchedules(@Valid @RequestBody ScheduleSaveRequest scheduleDto,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        scheduleService.createSchedule(userDetails.getId(),scheduleDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("일정 생성 성공");
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleDetailsResponse>getScheduleDetails(@PathVariable("scheduleId")Long scheduleId){
            ScheduleDetailsResponse response = scheduleService.findScheduleDetails(scheduleId);
            return ResponseEntity.ok(response);
    }

}
