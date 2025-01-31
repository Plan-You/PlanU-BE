package com.planu.group_meeting.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupInviteResponseDTO {
    private String invitedUsername;
    private Long groupId;
    private String groupName;
    private String groupImageUrl;
}