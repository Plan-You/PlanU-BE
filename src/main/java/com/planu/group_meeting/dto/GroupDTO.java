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
    @Setter
    @RequiredArgsConstructor
    public static class Member {
        private final String name;
        private final String username;
        private final String groupRole;
        private final String profileImage;
        private String friendStatus;
    }
}
