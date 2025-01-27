package com.planu.group_meeting.dto;

import lombok.Getter;


public class ParticipantDto {
    @Getter
    public static class ScheduleParticipantResponse{
        private String name;
        private String username;
        private String profileImage;
    }

    @Getter
    public static class UnregisteredParticipantResponse{
        private String name;
    }

}
