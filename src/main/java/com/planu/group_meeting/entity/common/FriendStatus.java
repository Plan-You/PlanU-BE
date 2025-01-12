package com.planu.group_meeting.entity.common;

public enum FriendStatus {
    NONE(0), REQUEST(1), RECEIVE(2), FRIEND(3);

    public final int value;

    FriendStatus(int value){
        this.value = value;
    }

    public static FriendStatus valueOf(int value){
        return switch (value) {
            case 0 -> NONE;
            case 1 -> REQUEST;
            case 2 -> RECEIVE;
            case 3 -> FRIEND;
            default -> throw new AssertionError("Unknown value: " + value);
        };
    }
}
