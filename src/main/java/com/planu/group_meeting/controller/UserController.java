package com.planu.group_meeting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planu.group_meeting.config.auth.CustomUserDetails;
import com.planu.group_meeting.controller.docs.UserDocs;
import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.dto.TokenDto;
import com.planu.group_meeting.dto.UserTermsDto;
import com.planu.group_meeting.service.UserService;
import com.planu.group_meeting.util.CookieUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.beans.PropertyEditorSupport;

import static com.planu.group_meeting.dto.UserDto.*;
import static com.planu.group_meeting.jwt.JwtFilter.AUTHORIZATION_HEADER;
import static com.planu.group_meeting.jwt.JwtFilter.BEARER_PREFIX;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController implements UserDocs {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<BaseResponse> createUser(@Valid @RequestBody SignUpRequest userDto) {
        userService.createUser(userDto);
        return BaseResponse.toResponseEntity(HttpStatus.CREATED, "회원가입 성공");
    }

    @GetMapping("/username/{username}/exists")
    public ResponseEntity<BaseResponse> checkDuplicatedUsername(@PathVariable("username") String username) {
        String resultMsg = Boolean.toString(userService.isDuplicatedUsername(username)); // Boolean -> String 변환
        return BaseResponse.toResponseEntity(HttpStatus.OK, resultMsg);
    }

    @PostMapping("/email-verification/sends")
    public ResponseEntity<BaseResponse> sendEmailCode(@Valid @RequestBody EmailSendRequest emailRequest) throws MessagingException {
        userService.sendCodeToEmail(emailRequest);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "인증 코드 전송 성공");
    }


    @PostMapping("/email-verification/verify")
    public ResponseEntity<BaseResponse> verifyEmailCode(@Valid @RequestBody EmailVerificationRequest emailVerificationDto) {
        userService.verifyEmailCode(emailVerificationDto);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "인증 성공");
    }

    @PostMapping("/profile")
    public ResponseEntity<BaseResponse> createUserProfile(@ModelAttribute @Valid UserRegistrationRequest userDto,
                                                          @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails) {

        userService.createUserProfile(userDetails.getId(), userDto, profileImage);
        return BaseResponse.toResponseEntity(HttpStatus.CREATED, "프로필 등록 성공");
    }

    @PutMapping("/profile")
    public ResponseEntity<BaseResponse> updateUserProfile(@ModelAttribute @Valid UserProfileUpdateRequest request,
                                                          @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updateUserProfile(userDetails.getId(), request, profileImage);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "프로필 수정 성공");
    }


    @GetMapping("/profile/exists")
    public ResponseEntity<BaseResponse> checkProfileExists(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String resultMsg = Boolean.toString(userService.isUserProfileCompleted(userDetails.getUsername()));
        return BaseResponse.toResponseEntity(HttpStatus.OK, resultMsg);
    }

    @PostMapping("/find-username")
    public ResponseEntity<BaseResponse> findUsername(@Valid @RequestBody EmailRequest emailRequest) {
        return BaseResponse.toResponseEntity(HttpStatus.OK, userService.findUsername(emailRequest));
    }

    @PostMapping("/find-password")
    public ResponseEntity<BaseResponse> findPassword(@Valid @RequestBody FindPasswordRequest findPasswordRequest) {
        userService.updatePassword(findPasswordRequest);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "비밀번호 변경 성공");
    }

    @PostMapping("/token/reissue")
    public ResponseEntity<BaseResponse> reissueAccessToken(HttpServletResponse response, HttpServletRequest request) {
        String refresh = CookieUtil.getCookieValue(request, "refresh");
        TokenDto tokenDTO = userService.reissueAccessToken(refresh);
        response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + tokenDTO.getAccess());
        CookieUtil.createCookie(response, "refresh", tokenDTO.getRefresh());
        return BaseResponse.toResponseEntity(HttpStatus.OK, "access 토콘 재발급 성공");
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        String refresh = CookieUtil.getCookieValue(request, "refresh");
        System.out.println("refresh 토큰 : " + refresh);
        userService.logout(refresh);
        response.addCookie(CookieUtil.deleteCookie("refresh"));
        response.addCookie(CookieUtil.deleteCookie("username"));
        return BaseResponse.toResponseEntity(HttpStatus.OK, "로그아웃 성공");
    }

    @GetMapping("/my-info")
    public ResponseEntity<UserInfoResponse> getUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserInfo(userDetails.getUsername()));
    }

    @PostMapping("/verify-password")
    public ResponseEntity<BaseResponse> verifyPassword(@RequestBody PasswordRequest passwordRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.verifyPassword(userDetails.getId(), passwordRequest);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "인증 성공");
    }

    @PostMapping("/validate-new-password")
    public ResponseEntity<BaseResponse> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest,
                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.validateNewPassword(userDetails.getId(), changePasswordRequest);
        return BaseResponse.toResponseEntity(HttpStatus.OK, "새 비밀번호 검증 성공");
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(UserTermsDto.TermsRequest.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    setValue(objectMapper.readValue(text, UserTermsDto.TermsRequest.class));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid format for TermsRequest");
                }
            }
        });
    }
}
