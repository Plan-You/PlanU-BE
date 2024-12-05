package com.planu.group_meeting.controller;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.AvailableDateDto.AvailableDatesRequest;
import com.planu.group_meeting.service.AvailableDateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/available-dates")
@RequiredArgsConstructor
public class AvailableDatesController {

    private final AvailableDateService availableDatesService;

    @PostMapping
    public ResponseEntity<String> saveAvailableDates(@RequestBody AvailableDatesRequest availableDatesDto,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        availableDatesService.createAvailableDates(userDetails.getId(), availableDatesDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("가능한 날짜 등록 성공");
    }

}
