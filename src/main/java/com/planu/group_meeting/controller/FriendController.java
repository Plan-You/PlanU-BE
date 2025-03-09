package com.planu.group_meeting.controller;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.controller.docs.FriendDocs;
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
public class FriendController implements FriendDocs {

    private final FriendService friendService;

    @PostMapping("/request")
    public ResponseEntity<BaseResponse> requestFriend(@RequestParam("username") String username,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        friendService.requestFriend(userDetails.getId(), username);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "친구요청 성공");
    }

    @PostMapping("/request-accept")
    public ResponseEntity<BaseResponse> acceptFriend(@RequestParam("username") String username,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {

        friendService.acceptFriend(userDetails.getId(), username);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "친구요청 수락 성공");
    }

    @DeleteMapping("/request-reject")
    public ResponseEntity<BaseResponse> rejectFriend(@RequestParam("username") String username,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        friendService.rejectFriend(userDetails.getId(), username);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "친구요청 거절 성공");
    }

    @DeleteMapping("/request-cancel")
    public ResponseEntity<BaseResponse> cancelFriendRequest(@RequestParam("username") String username,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        friendService.cancelFriendRequest(userDetails.getId(), username);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "친구요청 취소 성공");
    }

    @DeleteMapping
    public ResponseEntity<BaseResponse> deleteFriends(@RequestParam("username") String username,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        friendService.deleteFriend(userDetails.getId(), username);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "친구 삭제 성공");
    }

    @GetMapping
    public ResponseEntity<FriendListResponse> getFriendList(@RequestParam(value = "search", defaultValue = "") String keyword,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(friendService.getFriendList(userDetails.getId(), keyword));
    }

    @GetMapping("/request")
    public ResponseEntity<FriendListResponse> getFriendRequestList(@RequestParam(value = "search", defaultValue = "") String keyword, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(friendService.getFriendRequestList(userDetails.getId(), keyword));
    }

    @GetMapping("/receive")
    public ResponseEntity<FriendListResponse> getFriendReceiveList(@RequestParam(value = "search", defaultValue = "") String keyword, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(friendService.getFriendReceiveList(userDetails.getId(), keyword));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<FriendListResponse> getRecommendationFriends(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(friendService.getRecommendationFriends(userDetails.getId()));
    }

}
