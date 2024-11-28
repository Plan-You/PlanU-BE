package com.planu.group_meeting.exception.user;

public class ExpiredTokenException extends RuntimeException{
    public ExpiredTokenException() {
        super("만료된 토큰입니다.");
    }

    public ExpiredTokenException(String message) {
        super(message);
    }
}
