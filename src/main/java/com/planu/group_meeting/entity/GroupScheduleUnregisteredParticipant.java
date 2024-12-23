package com.planu.group_meeting.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GroupScheduleUnregisteredParticipant {
    private Long id;
    private Long groupScheduleId;
    private String participantName;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public GroupScheduleUnregisteredParticipant(Long groupScheduleId, String participantName) {
        this.groupScheduleId = groupScheduleId;
        this.participantName = participantName;
    }
}
