package com.planu.group_meeting.location.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberLocation {
    private String username;
    private String profileImage;
    private String name;
    private Double latitude;
    private Double longitude;
}
