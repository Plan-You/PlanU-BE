package com.planu.group_meeting.controller;

import com.planu.group_meeting.dto.UserDto;
import com.planu.group_meeting.exception.user.InvalidTokenException;
import com.planu.group_meeting.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<String>createUser(@Valid @RequestBody UserDto.SignUpRequest userDto){
        userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
    }

    @GetMapping("/username/{username}/exists")
    public ResponseEntity<Boolean>checkDuplicatedUsername(@PathVariable("username")String username){
        return ResponseEntity.ok(userService.isDuplicatedUsername(username));
    }

    @PostMapping("/email-verification/sends")
    public ResponseEntity<String>sendEmailCode(@RequestParam("email")String email) throws MessagingException {
        userService.sendCodeToEmail(email);
        // 이메일 형식 검증 기능 필요
        return ResponseEntity.status(HttpStatus.OK).body("인증 코드 전송 성공");
    }

    @PostMapping("/email-verification/verify")
    public ResponseEntity<String>verifyEmailCode(@RequestParam("email")String email,
                                                 @RequestParam("authCode")String authCode){
        userService.verifyEmailCode(email,authCode);
        return ResponseEntity.status(HttpStatus.OK).body("인증 성공");
    }

    @PostMapping("/profile")
    public ResponseEntity<String>createUserProfile(@RequestBody UserDto.UserProfileRequest userDto){
        userService.createUserProfile(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("프로필 등록 성공");
    }

    @PostMapping("/token/reissue")
    public ResponseEntity<String>reissueAccessToken(HttpServletResponse response, HttpServletRequest request){
        userService.reissueAccessToken(request,response);
        return ResponseEntity.status(HttpStatus.OK).body("access 토콘 재발급 성공");
    }



}
