package com.planu.group_meeting.exception.user;

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException() {
        super("유효하지 않은 토큰입니다.");
    }
    public InvalidTokenException(String message) {
        super(message);
    }
}
