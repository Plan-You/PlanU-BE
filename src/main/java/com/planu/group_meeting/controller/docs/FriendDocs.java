package com.planu.group_meeting.controller.docs;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.dto.FriendDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "FRIEND API", description = "친구 관련 API")
public interface FriendDocs {

    @Operation(summary = "친구 요청 보내기", description = "해당 유저에게 친구 요청을 보냅니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "친구 요청 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"resultCode\": 200,\n  \"resultMsg\": \"친구 요청 성공\"\n}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 사용자 아이디",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"resultCode\": 200,\n  \"resultMsg\": \"존재하지 않는 사용자 입니다.\"\n}")
                    )
            ),
            @ApiResponse(responseCode = "409", description = "유효하지 않은 요청",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "이미 친구요청을 보낸 경우",
                                            value = "{ \"resultCode\": 409, \"resultMsg\": \"이미 친구요청을 보냈습니다.\" }"),
                                    @ExampleObject(name = "이미 친구인 경우",
                                            value = "{ \"resultCode\": 409, \"resultMsg\": \"이미 친구입니다.\" }")
                            })
            )
    })
    ResponseEntity<BaseResponse> requestFriend(@RequestParam("username") String username,
                                               @AuthenticationPrincipal CustomUserDetails userDetails);


    @Operation(summary = "친구 요청 수락", description = "해당 유저의 친구 요청을 수락합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "친구 요청 수락 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"resultCode\": 200,\n  \"resultMsg\": \"친구 요청 수락 성공\"\n}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "친구 요청을 받은 상태가 아닌 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"resultCode\": 200,\n  \"resultMsg\": \"친구 요청 받은 상태가 아닙니다.\"\n}")
                    )
            )
    })
    ResponseEntity<BaseResponse> acceptFriend(@RequestParam("username") String username,
                                              @AuthenticationPrincipal CustomUserDetails userDetails);


    @Operation(summary = "친구 요청 거절", description = "해당 유저의 친구 요청을 거절합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "친구 요청 거절 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"resultCode\": 200,\n  \"resultMsg\": \"친구 요청 거절 성공\"\n}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "친구요청을 받지 않았는데 친구 요청 거절한 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"resultCode\": 400,\n  \"resultMsg\": \"친구 요청 받은 상태가 아닙니다.\"\n}")
                    )
            )
    })
    ResponseEntity<BaseResponse> rejectFriend(@RequestParam("username") String username,
                                              @AuthenticationPrincipal CustomUserDetails userDetails);


    @Operation(summary = "친구 요청 취소", description = "보낸 친구 요청을 취소합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "친구 요청 취소 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"resultCode\": 200,\n  \"resultMsg\": \"친구 요청 취소 성공\"\n}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 사용자 아이디",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"resultCode\": 404,\n  \"resultMsg\": \"존재하지 않는 사용자 입니다.\"\n}")
                    )
            )
    })
    ResponseEntity<BaseResponse> cancelFriendRequest(@RequestParam("username") String username,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails);


    @Operation(summary = "친구 삭제", description = "친구를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "친구 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"resultCode\": 200,\n  \"resultMsg\": \"친구 삭제 성공\"\n}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 사용자 아이디",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"resultCode\": 404,\n  \"resultMsg\": \"존재하지 않는 사용자 입니다.\"\n}")
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 친구가 아닌 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"resultCode\": 409,\n  \"resultMsg\": \"알 수 없는 요청입니다.\"\n}")
                    )
            )
    })
    ResponseEntity<BaseResponse> deleteFriends(@RequestParam("username") String username,
                                               @AuthenticationPrincipal CustomUserDetails userDetails);


    @Operation(summary = "친구 목록 조회", description = "로그인한 사용자의 친구 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "친구 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"totalFriends\": 2,\n  \"friends\": [\n    {\n      \"userId\": 123,\n      \"name\": \"이상준\",\n      \"username\": \"sangjun123\",\n      \"profileImageUrl\": \"https://example.com/profiles.jpg\"\n    },\n    {\n      \"userId\": 124,\n      \"name\": \"정재호\",\n      \"username\": \"jaeho123\",\n      \"profileImageUrl\": \"https://example.com/profiles.jpg\"\n    }]\n}")
                    )
            )
    })
    ResponseEntity<FriendDto.FriendListResponse> getFriendList(@RequestParam(value = "search", defaultValue = "") String keyword, @AuthenticationPrincipal CustomUserDetails userDetails);


    @Operation(summary = "친구 요청 받은 목록 조회", description = "사용자가 받은 친구 요청 목록을 조회합니다.", security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "친구 요청 받은 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"totalFriends\": 2,\n  \"friends\": [\n    {\n      \"userId\": 123,\n      \"name\": \"이상준\",\n      \"username\": \"sangjun123\",\n      \"profileImageUrl\": \"https://example.com/profiles.jpg\"\n    },\n    {\n      \"userId\": 124,\n      \"name\": \"정재호\",\n      \"username\": \"jaeho123\",\n      \"profileImageUrl\": \"https://example.com/profiles.jpg\"\n    }]\n}")
                    )
            )
    })
    ResponseEntity<FriendDto.FriendListResponse> getFriendRequestList(@RequestParam(value="search" , defaultValue = "") String keyword,@AuthenticationPrincipal CustomUserDetails userDetails);


    @Operation(summary = "친구 요청 보낸 목록 조회", description = "사용자가 보낸 친구 요청 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "친구 요청 보낸 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"totalFriends\": 2,\n  \"friends\": [\n    {\n      \"userId\": 123,\n      \"name\": \"이상준\",\n      \"username\": \"sangjun123\",\n      \"profileImageUrl\": \"https://example.com/profiles.jpg\"\n    },\n    {\n      \"userId\": 124,\n      \"name\": \"정재호\",\n      \"username\": \"jaeho123\",\n      \"profileImageUrl\": \"https://example.com/profiles.jpg\"\n    }]\n}")
                    )
            )
    })
    ResponseEntity<FriendDto.FriendListResponse> getFriendReceiveList(@RequestParam(value="search" , defaultValue = "") String keyword,@AuthenticationPrincipal CustomUserDetails userDetails);


}
