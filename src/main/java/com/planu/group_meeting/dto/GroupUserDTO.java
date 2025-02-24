package com.planu.group_meeting.dto;

import com.planu.group_meeting.entity.GroupUser;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GroupUserDTO {

    private Long userId;
    private Long groupId;
    private GroupUser.GroupRole groupRole;
    private Integer groupState;
    private LocalDateTime pinTime;
    private Boolean isPin;
    private Long version;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
