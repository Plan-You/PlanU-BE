package com.planu.group_meeting.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupResponseDTO {
    private Long groupId;
    private String groupName;
    private String leaderUsername;
    private String groupImageUrl;
    private String participant;
}
