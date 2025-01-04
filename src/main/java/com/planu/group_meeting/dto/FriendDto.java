package com.planu.group_meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class FriendDto {

    @Setter
    @Getter
    @AllArgsConstructor
    public static class FriendListResponse{
        private int totalFriends;
        private List<FriendInfo>friends;
    }

    @Getter
    @Setter
    public static class FriendInfo{
        private Long userId;
        private String name;
        private String username;
        private String profileImageUrl;
    }

}
