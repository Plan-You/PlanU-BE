package com.planu.group_meeting.controller.docs;

import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "USER API", description = "사용자 관련 API")
public interface UserDocs {

    @Operation(summary = "회원가입", description = "사용자 회원가입을 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 201, \"resultMsg\": \"회원가입 성공\" }"))),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패 - 입력 값이 올바르지 않음",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "아이디 유효성 검사 위배",
                                            value = "{ \"resultCode\": 400, \"resultMsg\": \"아이디는 5~12자의 영어 소문자와 숫자만 입력 가능합니다.\" }"),
                                    @ExampleObject(name = "비밀번호 유효성 검사 위배",
                                            value = "{ \"resultCode\": 400, \"resultMsg\": \"비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.\" }"),
                                    @ExampleObject(name = "이름 유효성 검사 위배",
                                            value = "{ \"resultCode\": 400, \"resultMsg\": \"이름은 2자 이상 6자 이하의 한글만 입력 가능합니다.\" }"),
                                    @ExampleObject(name = "이메일 유효성 검사 위배",
                                            value = "{ \"resultCode\": 400, \"resultMsg\": \"잘못된 이메일 형식입니다.\" }")
                            })
            ),
            @ApiResponse(responseCode = "409", description = "아이디 중복, 이메일 중복",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "아이디 중복",
                                            value = "{ \"resultCode\": 409, \"resultMsg\": \"사용자 아이디가 이미 존재합니다.\" }"),
                                    @ExampleObject(name = "이메일 중복",
                                            value = "{ \"resultCode\": 409, \"resultMsg\": \"이미 존재하는 이메일 입니다.\" }")
                            })
            ),
            @ApiResponse(responseCode = "400", description = "이메일 인증 미완료",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 400, \"resultMsg\": \"이메일 인증이 완료되지 않았습니다.\" }")))
    })
    ResponseEntity<BaseResponse> createUser(@Valid @RequestBody UserDto.SignUpRequest userDto);


    @Operation(summary = "아이디 중복 체크", description = "해당 아이디가 이미 존재하는지 확인합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "중복 여부 확인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"resultCode\": 200,\n  \"resultMsg\": \"true\"\n}")
                    )
            )
    })
    ResponseEntity<BaseResponse> checkDuplicatedUsername(@PathVariable("username") String username);


    @Operation(summary = "이메일 인증 코드 전송", description = "purpose 의 값을 register, findUsername, findPassword 중 인증 목적에 따라 설정하고 이메일로 인증 코드를 전송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 코드 전송 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 200, \"resultMsg\": \"인증 코드 전송 성공\" }"))),
            @ApiResponse(responseCode = "409", description = "회원가입을 위한 이메일 인증 코드 전송 시, 이미 존재하는 이메일",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 409, \"resultMsg\": \"이미 존재하는 이메일 입니다.\" }"))),
    })
    ResponseEntity<BaseResponse> sendEmailCode(@Valid @RequestBody UserDto.EmailSendRequest emailRequest) throws MessagingException;



    @Operation(summary = "이메일 인증 코드 검증", description = "purpose 의 값을 register, findUsername, findPassword 중 인증 목적에 따라 설정하고 이메일로 인증 코드를 검증합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 200, \"resultMsg\": \"인증 성공\" }"))),
            @ApiResponse(responseCode = "400", description = "잘못된 인증 코드, 인증코드 만료",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "잘못된 인증 코드",
                                            value = "{ \"resultCode\": 400, \"resultMsg\": \"유효하지 않은 인증코드입니다.\" }"),
                                    @ExampleObject(name = "인증 코드 만료",
                                            value = "{ \"resultCode\": 400, \"resultMsg\": \"인증번호가 만료되었습니다.\" }")
                            })
            )
    })
    ResponseEntity<BaseResponse> verifyEmailCode(@Valid @RequestBody UserDto.EmailVerificationRequest emailVerificationDto);


    @Operation(summary = "프로필 등록", description = "사용자 프로필 정보를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "프로필 등록 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 201, \"resultMsg\": \"프로필 등록 성공\" }"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청으로 인해 프로필 등록 실패",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "개인정보 보호 정책에 동의하지 않은 경우",
                                            value = "{ \"resultCode\": 400, \"resultMsg\": \"개인정보 보호 정책에 동의해야 합니다.\" }"),
                                    @ExampleObject(name = "서비스 이용 약관에 동의하지 않은 경우",
                                            value = "{ \"resultCode\": 400, \"resultMsg\": \"서비스 이용 약관에 동의해야 합니다.\" }"),
                                    @ExampleObject(name = "SNS 수신 동의 여부 잘못된 값",
                                            value = "{ \"resultCode\": 400, \"resultMsg\": \"SNS 수신 동의 여부는 'true' 또는 'false'만 입력 가능합니다.\" }"),
                                    @ExampleObject(name = "성별 필드 값이 잘못된 경우",
                                            value = "{ \"resultCode\": 400, \"resultMsg\": \"성별은 'M' 또는 'F'만 입력 가능합니다.\" }"),
                                    @ExampleObject(name = "허용되지 않는 파일 형식",
                                            value = "{ \"resultCode\": 400, \"resultMsg\": \"허용되지 않는 파일 형식입니다. JPEG, PNG, GIF 형식만 지원됩니다.\" }"),
                                    @ExampleObject(name = "파일 크기가 5MB를 초과한 경우",
                                            value = "{ \"resultCode\": 400, \"resultMsg\": \"파일 크기는 5MB를 초과할 수 없습니다.\" }")
                            })
            )
    })
    ResponseEntity<BaseResponse> createUserProfile(@ModelAttribute @Valid UserDto.UserRegistrationRequest userDto,
                                                  @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails);


    @Operation(summary = "프로필 여부 확인", description = "사용자의 프로필 등록 여부를 확인합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "중복 여부 확인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\n  \"resultCode\": 200,\n  \"resultMsg\": \"true\"\n}")
                    )
            )
    })
    ResponseEntity<BaseResponse> checkProfileExists(@AuthenticationPrincipal CustomUserDetails userDetails);


    @Operation(summary = "아이디 찾기", description = "사용자의 이메일을 통해 아이디(username)를 찾습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아이디 찾기 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 200, \"resultMsg\": \"사용자 아이디(username)\" }"))),
            @ApiResponse(responseCode = "400", description = "이메일 인증 미완료",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 400, \"resultMsg\": \"이메일 인증이 완료되지 않았습니다.\" }"))),
            @ApiResponse(responseCode = "404", description = "해당 이메일을 가진 사용자가 존재하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 404, \"resultMsg\": \"사용자를 찾을 수 없습니다.\" }")))
    })
    ResponseEntity<BaseResponse> findUsername(@Valid @RequestBody UserDto.EmailRequest emailRequest);

    @Operation(summary = "비밀번호 변경", description = "사용자가 새 비밀번호를 설정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 200, \"resultMsg\": \"비밀번호 변경 성공\" }"))),
            @ApiResponse(responseCode = "400", description = "이메일 인증 미완료",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 400, \"resultMsg\": \"이메일 인증이 완료되지 않았습니다.\" }"))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 404, \"resultMsg\": \"사용자를 찾을 수 없습니다.\" }"))),
            @ApiResponse(responseCode = "404", description = "입력한 아이디와 이메일이 사용자 정보와 일치하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 404, \"resultMsg\": \"입력한 아이디와 이메일이 사용자 정보와 일치하지 않습니다.\" }"))),
            @ApiResponse(responseCode = "400", description = "잘못된 비밀번호 형식",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 400, \"resultMsg\": \"비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.\" }")))
    })
    ResponseEntity<BaseResponse> findPassword(@Valid @RequestBody UserDto.ChangePasswordRequest changePasswordRequest);


    @Operation(summary = "Access Token 재발급", description = "유효한 Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Access Token 재발급 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 200, \"resultMsg\": \"access 토큰 재발급 성공\" }"))),
            @ApiResponse(responseCode = "401", description = "Refresh Token이 유효하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 403, \"resultMsg\": \"유효하지 않은 토큰입니다.\" }")))
    })
    ResponseEntity<BaseResponse> reissueAccessToken(HttpServletResponse response, HttpServletRequest request);

    @Operation(summary = "로그아웃", description = "사용자가 로그아웃합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 200, \"resultMsg\": \"로그아웃 성공\" }"))),
            @ApiResponse(responseCode = "403", description = "Refresh Token이 유효하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 200, \"resultMsg\": \"로그아웃 성공\" }")))
    })
    ResponseEntity<BaseResponse> logout(HttpServletRequest request, HttpServletResponse response);


    @Operation(summary = "내 프로필 조회", description = "현재 인증된 사용자의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.UserInfoResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"resultCode\": 404, \"resultMsg\": \"사용자를 찾을 수 없습니다.\" }")))
    })
    ResponseEntity<UserDto.UserInfoResponse>getUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails);
}