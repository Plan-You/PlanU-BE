package com.planu.group_meeting.exception.group;

public class UnauthorizedAccessException extends IllegalArgumentException {
    public UnauthorizedAccessException(String message) { super(message); }
}
