package com.planu.group_meeting.controller;
import com.planu.group_meeting.config.auth.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/")
    public String test(){
        return "hi";
    }

    @GetMapping("/profile")
    public ResponseEntity<String> getUserProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            return ResponseEntity.ok("로그인된 사용자: " + userDetails.getUsername());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자가 인증되지 않았습니다.");
        }
    }
}
