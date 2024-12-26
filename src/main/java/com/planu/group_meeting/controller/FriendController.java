package com.planu.group_meeting.controller;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.dto.FriendDto.FriendListResponse;
import com.planu.group_meeting.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/friends")
public class FriendController {

    private final FriendService friendService;

    @PostMapping
    public ResponseEntity<BaseResponse> requestFriend(@RequestParam("username") String username,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        friendService.requestFriend(userDetails.getId(), username);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "친구요청 성공");
    }

    @PostMapping("/accept")
    public ResponseEntity<BaseResponse> acceptFriend(@RequestParam("username") String username,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {

        friendService.acceptFriend(userDetails.getId(), username);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "친구요청 수락 성공");
    }

    @DeleteMapping("/reject")
    public ResponseEntity<BaseResponse>rejectFriend(@RequestParam("username")String username,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails){
        friendService.rejectFriend(userDetails.getId(), username);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "친구요청 거절 성공");
    }

    @GetMapping
    public ResponseEntity<FriendListResponse> getFriendList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(friendService.getFriendList(userDetails.getId()));
    }

}
