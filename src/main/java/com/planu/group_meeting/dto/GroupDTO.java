package com.planu.group_meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

public class GroupDTO {

    @Getter
    @AllArgsConstructor
    public static class GroupMembersResponse {
        List<Member> members;
    }

    @Getter
    @AllArgsConstructor
    public static class NonGroupFriendsResponse {
        List<NonGroupFriend> nonGroupFriends;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class Member {
        private final String name;
        private final String username;
        private final String groupRole;
        private final String profileImage;
        private String friendStatus;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class NonGroupFriend {
        private String name;
        private String username;
        private String profileImage;
        private String status;
    }

}
