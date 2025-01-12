package com.planu.group_meeting.controller.docs;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.AvailableDateDto;
import com.planu.group_meeting.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "AVAILABLE_DATE API", description = "가능한 날짜 관련 API")
public interface AvailableDateDocs {


    @Operation(summary = "가능한 약속 날짜 저장 및 삭제",
            description = "사용자가 약속 가능한 날짜를 선택하여 추가 및 삭제하는 API입니다. " + "이미 저장했던 날짜는 삭제되며, 추가하지 않았던 날짜 데이터를 요청하여 가능 날짜를 추가하거나 삭제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "저장 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"resultCode\": 201,\n  \"resultMsg\": \"저장 성공\"\n}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "현재 날짜 이전의 날짜를 선택한 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"resultCode\": 400,\n  \"resultMsg\": \"지난 날짜는 선택할 수 없습니다.\"\n}")
                    )
            )
    })
    ResponseEntity<BaseResponse> saveAvailableDates(@RequestBody AvailableDateDto.AvailableDatesRequest availableDatesDto,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails);


    @Operation(summary = "가능한 약속 날짜 조회", description = "사용자가 표시한 가능한 날짜들을 응답합니다. " + "startDate와 endDate 범위의 가능한 날짜들을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "가능한 날짜 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "[\n  \"2025-11-22\",\n  \"2025-11-23\",\n  \"2025-11-24\"\n]"
                            )
                    )
            )
    })
    ResponseEntity<List<LocalDate>> getAvailableDates(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @AuthenticationPrincipal CustomUserDetails userDetails);

}
