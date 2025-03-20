package com.planu.group_meeting.location.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupMemberLocationResponse {
    List<GroupMemberLocation> groupMemberLocations;
}
