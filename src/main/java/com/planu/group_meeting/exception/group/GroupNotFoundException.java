package com.planu.group_meeting.exception.group;

public class GroupNotFoundException extends IllegalArgumentException {
    public GroupNotFoundException(String message){ super(message); }
}
