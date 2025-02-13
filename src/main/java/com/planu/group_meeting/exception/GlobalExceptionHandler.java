package com.planu.group_meeting.exception;

import com.planu.group_meeting.dto.BaseResponse;
import com.planu.group_meeting.exception.file.InvalidFileTypeException;
import com.planu.group_meeting.exception.group.GroupNotFoundException;
import com.planu.group_meeting.exception.group.InvalidInputException;
import com.planu.group_meeting.exception.group.UnauthorizedAccessException;
import com.planu.group_meeting.exception.schedule.PastDateValidationException;
import com.planu.group_meeting.exception.schedule.ScheduleNotFoundException;
import com.planu.group_meeting.exception.user.*;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<BaseResponse>handleNotFoundException(NotFoundException e){
        return BaseResponse.toResponseEntity(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<BaseResponse>handleIllegalStateException(IllegalStateException e){
        return BaseResponse.toResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
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

    @ExceptionHandler(UnauthorizedResourceException.class)
    public ResponseEntity<BaseResponse>handleUnauthorizedResourceException(UnauthorizedResourceException e){
        return BaseResponse.toResponseEntity(HttpStatus.UNAUTHORIZED,"권한이 없습니다.");
    }

    @ExceptionHandler(FriendRequestNotFoundException.class)
    public ResponseEntity<BaseResponse>handleFriendRequestNotFoundException(FriendRequestNotFoundException e){
        return BaseResponse.toResponseEntity(HttpStatus.BAD_REQUEST,"친구 요청 받은 상태가 아닙니다.");
    }

    @ExceptionHandler(NotFriendException.class)
    public ResponseEntity<BaseResponse>handleNotFriendException(NotFriendException e){
        return BaseResponse.toResponseEntity(HttpStatus.BAD_REQUEST,"친구로 등록된 사용자가 아닙니다.");
    }

    @ExceptionHandler(DuplicatedRequestException.class)
    public ResponseEntity<BaseResponse>handleDuplicatedRequestException(DuplicatedRequestException e){
        return BaseResponse.toResponseEntity(HttpStatus.CONFLICT,e.getMessage());
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
    public ResponseEntity<BaseResponse> handleInvalidInputException(InvalidInputException e) {
        return BaseResponse.toResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return BaseResponse.toResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<BaseResponse> handleGroupNotFoundException(GroupNotFoundException e) {
        return BaseResponse.toResponseEntity(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<BaseResponse> handleUnauthorizedAccessException(UnauthorizedAccessException e) {
        return BaseResponse.toResponseEntity(HttpStatus.FORBIDDEN, e.getMessage());
    }
}
