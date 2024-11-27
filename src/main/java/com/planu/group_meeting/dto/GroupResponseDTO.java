package com.planu.group_meeting.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupResponseDTO {
    private Long groupId;
    private String groupName;
    private String leaderUserName;
    private String groupImageUrl;
}
