package com.planu.group_meeting.chat.controller.swagger;

import com.planu.group_meeting.chat.dto.request.ChatFileRequest;
import com.planu.group_meeting.chat.dto.response.ChatMessageResponse;
import com.planu.group_meeting.chat.dto.response.ChatRoomResponse;
import com.planu.group_meeting.chat.dto.response.GroupedChatMessages;
import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.dto.DataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "CHAT API", description = "채팅 API")
public interface ChatDocs{

    @Operation(summary = "채팅방 목록 조회", description = "채팅방 목록을 보여줍니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅방 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\n" +
                                    "  \"data\": [\n" +
                                    "    {\n" +
                                    "      \"groupId\": 1,\n" +
                                    "      \"groupName\": \"Study Group\",\n" +
                                    "      \"groupImageUrl\": \"https://example.com/group1.png\",\n" +
                                    "      \"participant\": 10,\n" +
                                    "      \"isPin\": true,\n" +
                                    "      \"lastChat\": \"안녕하세요.\",\n" +
                                    "      \"lastChatDate\": \"2025-02-16\",\n" +
                                    "      \"lastChatTime\": \"15:48\",\n" +
                                    "      \"unreadChats\": 12\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "      \"groupId\": 2,\n" +
                                    "      \"groupName\": \"Workout Group\",\n" +
                                    "      \"groupImageUrl\": \"https://example.com/group2.png\",\n" +
                                    "      \"participant\": 15,\n" +
                                    "      \"isPin\": true,\n" +
                                    "      \"lastChat\": \"\",\n" +
                                    "      \"lastChatDate\": \"\",\n" +
                                    "      \"lastChatTime\": \"\",\n" +
                                    "      \"unreadChats\": 0\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "      \"groupId\": 3,\n" +
                                    "      \"groupName\": \"Gaming Group\",\n" +
                                    "      \"groupImageUrl\": \"https://example.com/group3.png\",\n" +
                                    "      \"participant\": 8,\n" +
                                    "      \"isPin\": false,\n" +
                                    "      \"lastChat\": \"\",\n" +
                                    "      \"lastChatDate\": \"\",\n" +
                                    "      \"lastChatTime\": \"\",\n" +
                                    "      \"unreadChats\": 0\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}\n")))
    })
    @ResponseBody
    @GetMapping("/chats")
    public ResponseEntity<DataResponse<List<ChatRoomResponse>>> chatRooms(@AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "채팅 조회", description = "채팅을 최대 50개 조회합니다.\nmessageId보다 이전 메시지를 조회하며, 없으면 가장 최근 메시지부터 가져옵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\n" +
                                    "  \"data\": [\n" +
                                    "    {\n" +
                                    "      \"chatDate\": \"2025-03-18\",\n" +
                                    "      \"messages\": [\n" +
                                    "        {\n" +
                                    "          \"type\": 1,\n" +
                                    "          \"messageId\": 101,\n" +
                                    "          \"message\": \"안녕하세요!\",\n" +
                                    "          \"sender\": \"UserA\",\n" +
                                    "          \"unReadCount\": 3,\n" +
                                    "          \"chatTime\": \"10:30\",\n" +
                                    "          \"profileImageURL\": null\n" +
                                    "        },\n" +
                                    "        {\n" +
                                    "          \"type\": 1,\n" +
                                    "          \"messageId\": 102,\n" +
                                    "          \"message\": \"오늘 회의 몇 시에 하시나요?\",\n" +
                                    "          \"sender\": \"UserB\",\n" +
                                    "          \"unReadCount\": 1,\n" +
                                    "          \"chatTime\": \"11:00\",\n" +
                                    "          \"profileImageURL\": null\n" +
                                    "        }\n" +
                                    "      ]\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "      \"chatDate\": \"2025-03-17\",\n" +
                                    "      \"messages\": [\n" +
                                    "        {\n" +
                                    "          \"type\": 2,\n" +
                                    "          \"messageId\": 103,\n" +
                                    "          \"message\": null,\n" +
                                    "          \"sender\": \"UserC\",\n" +
                                    "          \"unReadCount\": 0,\n" +
                                    "          \"chatTime\": \"11:05\",\n" +
                                    "          \"profileImageURL\": \"https://example.com/profile/userC.jpg\"\n" +
                                    "        }\n" +
                                    "      ]\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}")))
    })
    @ResponseBody
    @GetMapping("/chats/messages")
    public ResponseEntity<DataResponse<List<GroupedChatMessages>>> getChats(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("groupId") Long groupId,
            @RequestParam(value = "messageId", required = false) Long messageId);

    @Operation(summary = "채팅 메시지 갱신", description = "채팅 메시지별 읽지않은 사람 수를 보여줍니다\nstartId ~ endId")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\n" +
                                    "  \"data\": [\n" +
                                    "    {\n" +
                                    "      \"type\": 4,\n" +
                                    "      \"messageId\": 201,\n" +
                                    "      \"message\": null,\n" +
                                    "      \"sender\": null,\n" +
                                    "      \"unReadCount\": 5,\n" +
                                    "      \"chatDate\": null,\n" +
                                    "      \"chatTime\": null,\n" +
                                    "      \"profileImageURL\": null\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "      \"type\": 4,\n" +
                                    "      \"messageId\": 202,\n" +
                                    "      \"message\": null,\n" +
                                    "      \"sender\": null,\n" +
                                    "      \"unReadCount\": 2,\n" +
                                    "      \"chatDate\": null,\n" +
                                    "      \"chatTime\": null,\n" +
                                    "      \"profileImageURL\": null\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "      \"type\": 4,\n" +
                                    "      \"messageId\": 203,\n" +
                                    "      \"message\": null,\n" +
                                    "      \"sender\": null,\n" +
                                    "      \"unReadCount\": 0,\n" +
                                    "      \"chatDate\": null,\n" +
                                    "      \"chatTime\": null,\n" +
                                    "      \"profileImageURL\": null\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}\n")))
    })
    @ResponseBody
    @GetMapping("/chats/messages/update")
    public ResponseEntity<DataResponse<List<ChatMessageResponse>>> getUpdateChats(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("groupId") Long groupId,
            @RequestParam("startId") Long startId,
            @RequestParam("endId") Long endId
    );

    @Operation(summary = "새로운 메시지 수 조회", description = "전체 채팅방에서 유저가 읽지않은 메시지 수를 조회합니다.")
    @ResponseBody
    @GetMapping("/chats/new")
    public ResponseEntity<Integer> countNewChat(@AuthenticationPrincipal CustomUserDetails userDetails);


    @Operation(summary = "사진 업로드 및 전송(채팅방)", description = "사진을 저장 한 후 채팅방에 메시지를 보냅니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사진 업로드 및 전송 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\n" +
                                    "\t\"resultCode\": 200,\n" +
                                    "  \"resultMsg\": \"사진 전송 및 업로드 성공\"\n" +
                                    "}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "잘못된 groupId 를 전달 한 경우",
                                            value = "{\n" +
                                                    "\t\"resultCode\": 400,\n" +
                                                    "  \"resultMsg\": \"잘못된 groupId\"\n" +
                                                    "}")
                            })
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "사진 업로드 및 전송. `form-data` 형식으로 요청해야 합니다.",
            content = @Content(
                    mediaType = "multipart/form-data",
                    schema = @Schema(implementation = ChatFileRequest.class)
            )
    )
    @ResponseBody
    @PostMapping("/chats/file")
    public ResponseEntity<BaseResponse> chatFileUpload(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                       @ModelAttribute ChatFileRequest chatFileRequest);
}
