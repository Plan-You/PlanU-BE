package com.planu.group_meeting.dto;

import lombok.*;

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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableMemberInfos {
        private List<AvailableMemberInfo> availableMemberInfos;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class AvailableMemberInfo {
        private String name;
        private String profileImage;
        private List<String> availableDates;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AvailableDateInfos {
        private List<AvailableDateInfo> availableDateInfos;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class AvailableDateInfo {
        private String availableDate;
        private List<String> memberNames;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class GroupInfo {
        private String groupName;
        private String groupImage;
        private Boolean isPin;
    }
}
