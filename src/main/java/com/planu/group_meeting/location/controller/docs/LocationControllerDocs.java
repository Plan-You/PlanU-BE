package com.planu.group_meeting.location.controller.docs;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.location.dto.request.LocationDTO;
import com.planu.group_meeting.location.dto.response.GroupMemberLocationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "LOCATION API", description = "위치 공유 API")
public interface LocationControllerDocs {

    @Operation(summary = "위치 갱신", description = "현재 내 위치를 갱신합니다.")
    @ApiResponses({
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "위치 갱신 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = """
                                                    {
                                                        "resultCode": 200,
                                                        "resultMsg": "위치 갱신 성공"
                                                    }
                                                    """
                                    )
                            )
                    )
    })
    ResponseEntity<BaseResponse> updateLocation(
            @RequestBody LocationDTO locationDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(summary = "그룹원 위치 조회", description = "그룹원의 위치 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse (
                    responseCode = "200",
                    description = "그룹원 위치 정보 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema (
                                    example = """
                                        {
                                            "GroupMemberLocation": [
                                                {
                                                    "username": "chlwnsgur",
                                                    "name": "최준혁",
                                                    "profileImage": "http://example.com/chlwnsgur",
                                                    "latitude": 52.2333,
                                                    "longitude": 21.2311
                                                },
                                                {
                                                    "username": "wjdwogh",
                                                    "name": "정재호",
                                                    "profileImage": "http://example.com/wjdwogh",
                                                    "latitude": 21.2323,
                                                    "longitude": 23.2222
                                                },
                                                {
                                                    "username": "rlaehgk",
                                                    "name": "김도하",
                                                    "profileImage": "http://example.com/rlaehgk",
                                                    "latitude": 44.2123,
                                                    "longitude": 32.3212
                                                }
                                            ]
                                        }
                                        """
                            )
                    )
            ),
            @ApiResponse
                    (
                            responseCode = "404",
                            description = "그룹/그룹 일정을 찾을 수 없는 경우",
                            content = @Content
                                    (
                                            mediaType = "application/json",
                                            examples =
                                                    {
                                                            @ExampleObject
                                                                    (
                                                                            name = "그룹을 찾을 수 없음",
                                                                            value = """
                                                                                            {
                                                                                                "resultCode": 404,
                                                                                                "resultMsg": "그룹을 찾을 수 없습니다."
                                                                                            }
                                                                                            """
                                                                    ),
                                                            @ExampleObject
                                                                    (
                                                                            name = "그룹 일정을 찾을 수 없음",
                                                                            value = """
                                                                                            {
                                                                                                "resultCode": 404,
                                                                                                "resultMsg": "그룹 일정을 찾을 수 없습니다."
                                                                                            }
                                                                                            """
                                                                    )
                                                    }
                                    )
                    ),
            @ApiResponse
                    (
                            responseCode = "403",
                            description = "그룹원이 아닌 경우",
                            content = @Content
                                    (
                                            mediaType = "application/json",
                                            schema = @Schema
                                                    (
                                                            example = """
                                                                            {
                                                                                "ResultCode": 403,
                                                                                "ResultMsg": "접근 권한이 없습니다."
                                                                            }
                                                                            """
                                                    )
                                    )
                    )
    })
    ResponseEntity<GroupMemberLocationResponse> getLocation(
            @PathVariable("groupId") Long groupId,
            @PathVariable("scheduleId") Long scheduleId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
