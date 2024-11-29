package com.planu.group_meeting.controller;

import com.planu.group_meeting.dto.TokenDto;
import com.planu.group_meeting.dto.UserDto;
import com.planu.group_meeting.dto.UserDto.UserProfileImageRequest;
import com.planu.group_meeting.service.UserService;
import com.planu.group_meeting.service.file.S3Uploader;
import com.planu.group_meeting.util.CookieUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.planu.group_meeting.jwt.JwtFilter.AUTHORIZATION_HEADER;
import static com.planu.group_meeting.jwt.JwtFilter.BEARER_PREFIX;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<String> createUser(@Valid @RequestBody UserDto.SignUpRequest userDto) {
        userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
    }

    @GetMapping("/username/{username}/exists")
    public ResponseEntity<Boolean> checkDuplicatedUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(userService.isDuplicatedUsername(username));
    }

    @PostMapping("/email-verification/sends")
    public ResponseEntity<String> sendEmailCode(@RequestBody UserDto.EmailRequest emailDto) throws MessagingException {
        userService.sendCodeToEmail(emailDto);
        return ResponseEntity.status(HttpStatus.OK).body("인증 코드 전송 성공");
    }

    @PostMapping("/email-verification/verify")
    public ResponseEntity<String> verifyEmailCode(@RequestBody UserDto.EmailVerificationRequest emailVerificationDto) {
        userService.verifyEmailCode(emailVerificationDto);
        return ResponseEntity.status(HttpStatus.OK).body("인증 성공");
    }

    @PostMapping("/profile")
    public ResponseEntity<String> createUserProfile(@RequestBody UserDto.UserProfileRequest userDto) {
        userService.createUserProfile(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("프로필 등록 성공");
    }

    @PutMapping("/profile-image")
    public ResponseEntity<String> updateProfileImage(@ModelAttribute UserProfileImageRequest userDto) {
        return ResponseEntity.ok(userService.updateUserProfileImage(userDto));
    }


    @PostMapping("/token/reissue")
    public ResponseEntity<String> reissueAccessToken(HttpServletResponse response, HttpServletRequest request) {
        String refresh = CookieUtil.getCookieValue(request, "refresh");
        TokenDto tokenDTO = userService.reissueAccessToken(refresh);
        response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + tokenDTO.getAccess());
        response.addCookie(CookieUtil.createCookie("refresh", tokenDTO.getRefresh()));
        return ResponseEntity.status(HttpStatus.OK).body("access 토콘 재발급 성공");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String refresh = CookieUtil.getCookieValue(request, "refresh");
        userService.logout(refresh);
        response.addCookie(CookieUtil.deleteCookie("refresh"));
        return ResponseEntity.status(HttpStatus.OK).body("로그아웃 성공");
    }

}
