package com.planu.group_meeting.exception;

import com.planu.group_meeting.exception.user.DuplicatedEmailException;
import com.planu.group_meeting.exception.user.DuplicatedUsernameException;
import com.planu.group_meeting.exception.user.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicatedUsernameException.class)
    public final ResponseEntity<String>handleDuplicatedUsernameException(DuplicatedUsernameException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("사용자 아이디가 이미 존재합니다.");
    }

    @ExceptionHandler(DuplicatedEmailException.class)
    public final ResponseEntity<String>handleDuplicatedEmailException(DuplicatedEmailException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("이메일이 이미 존재합니다.");
    }

    @ExceptionHandler(InvalidTokenException.class)
    public final ResponseEntity<String>handleInvalidTokenException(InvalidTokenException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다");
    }

}
