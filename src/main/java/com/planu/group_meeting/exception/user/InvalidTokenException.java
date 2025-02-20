package com.planu.group_meeting.exception.user;

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(String message) {
        super(message);
    }
}
