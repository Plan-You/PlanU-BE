package com.planu.group_meeting.controller;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/friends")
public class FriendController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<BaseResponse> requestFriend(@RequestParam("username") String username,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.requestFriend(userDetails.getId(), username);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "친구요청 성공");
    }

    @PostMapping("/accept")
    public ResponseEntity<BaseResponse> acceptFriend(@RequestParam("username") String username,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {

        userService.acceptFriend(userDetails.getId(), username);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "친구요청 수락 성공");
    }

}
