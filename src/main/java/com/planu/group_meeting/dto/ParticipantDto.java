package com.planu.group_meeting.dto;

import lombok.Getter;


public class ParticipantDto {
    @Getter
    public static class ScheduleParticipantResponse{
        private Long id;
        private String name;
    }
    @Getter
    public static class UnregisteredParticipantResponse{
        private String name;
    }

}
