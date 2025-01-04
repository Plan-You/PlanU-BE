package com.planu.group_meeting.exception.user;

public class DuplicatedRequestException extends RuntimeException {
    public DuplicatedRequestException(String message) {
        super(message);
    }
}
