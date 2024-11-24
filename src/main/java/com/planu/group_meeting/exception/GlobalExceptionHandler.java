package com.planu.group_meeting.exception;

import com.planu.group_meeting.exception.Group.InvalidInputException;
import com.planu.group_meeting.exception.user.*;
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
        return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 이메일 입니다.");
    }

    @ExceptionHandler(InvalidTokenException.class)
    public final ResponseEntity<String>handleInvalidTokenException(InvalidTokenException e){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("유효하지 않은 토큰입니다");
    }

    @ExceptionHandler(ExpiredAuthCodeException.class)
    public final ResponseEntity<String>handleExpiredAuthCodeException(ExpiredAuthCodeException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증번호가 만료되었습니다");
    }

    @ExceptionHandler(InvalidAuthCodeException.class)
    public final ResponseEntity<String>handleInvalidAuthCodeException(InvalidAuthCodeException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 인증코드입니다");
    }

    @ExceptionHandler(UnverifiedEmailException.class)
    public final ResponseEntity<String>handleUnverifiedEmailException(UnverifiedEmailException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일 인증이 완료되지 않았습니다.");
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<String> handleInvalidInputException(InvalidInputException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
