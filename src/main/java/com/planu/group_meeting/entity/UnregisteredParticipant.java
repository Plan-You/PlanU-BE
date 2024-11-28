package com.planu.group_meeting.entity;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class UnregisteredParticipant {
    private Long id;
    private Long scheduleId;
    private String participantName;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public UnregisteredParticipant(Long scheduleId, String participantName){
        this.scheduleId = scheduleId;
        this.participantName = participantName;
    }
}
