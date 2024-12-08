package com.planu.group_meeting.exception;

import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.exception.group.InvalidInputException;
import com.planu.group_meeting.exception.file.InvalidFileTypeException;
import com.planu.group_meeting.exception.schedule.PastDateValidationException;
import com.planu.group_meeting.exception.schedule.ScheduleNotFoundException;
import com.planu.group_meeting.exception.user.*;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            errorMessage.append(error.getDefaultMessage()).append(" ");
        });

        return BaseResponse.toResponseEntity(HttpStatus.BAD_REQUEST, errorMessage.toString());
    }

    @ExceptionHandler(DuplicatedUsernameException.class)
    public ResponseEntity<BaseResponse> handleDuplicatedUsernameException(DuplicatedUsernameException e) {
        return BaseResponse.toResponseEntity(HttpStatus.CONFLICT, "사용자 아이디가 이미 존재합니다.");
    }

    @ExceptionHandler(DuplicatedEmailException.class)
    public ResponseEntity<BaseResponse> handleDuplicatedEmailException(DuplicatedEmailException e) {
        return BaseResponse.toResponseEntity(HttpStatus.CONFLICT, "이미 존재하는 이메일 입니다.");
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<BaseResponse> handleInvalidTokenException(InvalidRefreshTokenException e) {
        return BaseResponse.toResponseEntity(HttpStatus.FORBIDDEN, "유효하지 않은 토큰입니다.");
    }

    @ExceptionHandler(ExpiredAuthCodeException.class)
    public ResponseEntity<BaseResponse> handleExpiredAuthCodeException(ExpiredAuthCodeException e) {
        return BaseResponse.toResponseEntity(HttpStatus.BAD_REQUEST, "인증번호가 만료되었습니다.");
    }

    @ExceptionHandler(InvalidAuthCodeException.class)
    public ResponseEntity<BaseResponse> handleInvalidAuthCodeException(InvalidAuthCodeException e) {
        return BaseResponse.toResponseEntity(HttpStatus.BAD_REQUEST, "유효하지 않은 인증코드입니다.");
    }

    @ExceptionHandler(UnverifiedEmailException.class)
    public ResponseEntity<BaseResponse> handleUnverifiedEmailException(UnverifiedEmailException e) {
        return BaseResponse.toResponseEntity(HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않았습니다.");
    }

    @ExceptionHandler(NotFoundUserException.class)
    public ResponseEntity<BaseResponse> handleNotFoundUserException(NotFoundUserException e) {
        return BaseResponse.toResponseEntity(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
    }

    @ExceptionHandler(EmailMismatchException.class)
    public ResponseEntity<BaseResponse> handleEmailMismatchException(EmailMismatchException e) {
        return BaseResponse.toResponseEntity(HttpStatus.NOT_FOUND, "입력한 아이디와 이메일이 사용자 정보와 일치하지 않습니다.");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<BaseResponse> handleFileSizeExceededException(MaxUploadSizeExceededException e) {
        return BaseResponse.toResponseEntity(HttpStatus.BAD_REQUEST, "파일 크기는 5MB를 초과할 수 없습니다.");
    }

    @ExceptionHandler(InvalidFileTypeException.class)
    public ResponseEntity<BaseResponse> handleInvalidFileTypeException(InvalidFileTypeException e) {
        return BaseResponse.toResponseEntity(HttpStatus.BAD_REQUEST, "허용되지 않는 파일 형식입니다. JPEG, PNG, GIF 형식만 지원됩니다.");
    }

    @ExceptionHandler(ScheduleNotFoundException.class)
    public ResponseEntity<BaseResponse> handleScheduleNotFoundException(ScheduleNotFoundException e) {
        return BaseResponse.toResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(PastDateValidationException.class)
    public ResponseEntity<BaseResponse> handlePastDateValidationException(PastDateValidationException e) {
        return BaseResponse.toResponseEntity(HttpStatus.BAD_REQUEST, "지난 날짜는 선택할 수 없습니다.");
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<String> handleInvalidInputException(InvalidInputException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
