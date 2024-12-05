package com.planu.group_meeting.controller;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.AvailableDateDto;
import com.planu.group_meeting.dto.AvailableDateDto.AvailableDatesRequest;
import com.planu.group_meeting.service.AvailableDateService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/available-dates")
@RequiredArgsConstructor
public class AvailableDateController {

    private final AvailableDateService availableDatesService;

    @PostMapping
    public ResponseEntity<String> saveAvailableDates(@RequestBody AvailableDatesRequest availableDatesDto,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        availableDatesService.createAvailableDates(userDetails.getId(), availableDatesDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("가능한 날짜 등록 성공");
    }

    @GetMapping
    public ResponseEntity<List<LocalDate>> getAvailableDates(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @AuthenticationPrincipal CustomUserDetails userDetails){

        return ResponseEntity.ok(availableDatesService.findAvailableDates(userDetails.getId(),startDate,endDate));
    }

}
