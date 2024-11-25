package com.planu.group_meeting.controller;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.ScheduleDto;
import com.planu.group_meeting.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    @PostMapping
    public ResponseEntity<String>createSchedules(@Valid @RequestBody ScheduleDto.ScheduleSaveRequest scheduleDto,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        System.out.println(userDetails.getId());
        scheduleService.createSchedule(userDetails.getId(),scheduleDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("일정 생성 성공");
    }

}
