package com.planu.group_meeting.controller.docs;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.dto.GroupScheduleCommentDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "GROUP SCHEDULE COMMENT API", description = "그룹 일정 댓글 API")
public interface GroupScheduleCommentDocs {

    @Operation(summary = "그룹 일정 댓글 생성 API", description = "그룹 일정 댓글을 생성합니다.")
    @ApiResponses
            ({
                    @ApiResponse
                            (
                                    responseCode = "201",
                                    description = "그룹 일정 댓글 생성 성공",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "resultCode": 201,
                                                                                "resultMsg": "그룹 일정 댓글 생성 성공"
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
    ResponseEntity<BaseResponse> create(@PathVariable("groupId") Long groupId,
                                        @PathVariable("scheduleId") Long groupScheduleId,
                                        @AuthenticationPrincipal CustomUserDetails userDetails,
                                        @RequestBody GroupScheduleCommentDTO.GroupScheduleCommentRequest groupScheduleComment);

    @Operation(summary = "그룹 일정 댓글을 조회 API", description = "그룹 일정 댓글을 조회합니다.")
    @ApiResponses
            ({
                    @ApiResponse
                            (
                                    responseCode = "200",
                                    description = "그룹 일정 댓글 조회 성공",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "comments": [
                                                                                    {
                                                                                        "id": 1,
                                                                                        "username": "chlwnsgur",
                                                                                        "name": "최준혁",
                                                                                        "timestamp": "10분 전",
                                                                                        "message": "니얼굴",
                                                                                        "isMyComment": true
                                                                                    },
                                                                                    {
                                                                                        "id": 2,
                                                                                        "username": "wjdwogh",
                                                                                        "name": "정재호",
                                                                                        "timestamp": "11분 전",
                                                                                        "message": "아 뭐하는데?",
                                                                                        "isMyComment": false
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
    ResponseEntity<Map<String, Object>> getAllComments(@PathVariable("groupId") Long groupId,
                                                       @PathVariable("scheduleId") Long groupScheduleId,
                                                       @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "그룹 일정 댓글 삭제 API", description = "그룹 일정 댓글을 삭제합니다.")
    @ApiResponses
            ({
                    @ApiResponse
                            (
                                    responseCode = "200",
                                    description = "그룹 일정 댓글 삭제 성공",
                                    content = @Content
                                            (
                                                    mediaType = "application/json",
                                                    schema = @Schema
                                                            (
                                                                    example = """
                                                                            {
                                                                                "resultCode": 200,
                                                                                "resultMsg": "그룹 일정 댓글 삭제 성공"
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
    ResponseEntity<BaseResponse> deleteCommentById(@PathVariable("groupId") Long groupId,
                                                   @PathVariable("scheduleId") Long groupScheduleId,
                                                   @PathVariable("commentId") Long commentId,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails);
}
