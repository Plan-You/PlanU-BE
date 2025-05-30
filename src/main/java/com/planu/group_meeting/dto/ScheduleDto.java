package com.planu.group_meeting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.planu.group_meeting.dto.ParticipantDto.ScheduleParticipantResponse;
import com.planu.group_meeting.dto.ParticipantDto.UnregisteredParticipantResponse;
import com.planu.group_meeting.entity.Schedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDto {

    @Data
    public static class ScheduleSaveRequest {

        @NotBlank(message = "일정 제목은 필수 입력 값입니다.")
        @Size(max = 20, message = "일정 제목은 20자 이내로 입력해주세요.")
        @Pattern(regexp = "^[^\\n]*$", message = "일정 제목은 줄넘김 없이 입력해주세요.")
        private String title;

        @NotNull(message = "시작 날짜와 시간은 필수 입력 값입니다.")
        private LocalDateTime startDateTime;

        @NotNull(message = "종료 날짜와 시간은 필수 입력 값입니다.")
        private LocalDateTime endDateTime;

        private String color;

        private String location;

        private double latitude;

        private double longitude;

        @Size(max = 100, message = "메모는 100자 이내로 입력해주세요.")
        private String memo;

        private List<String> participants;

        private List<String> unregisteredParticipants;

        public Schedule toEntity(Long userId) {
            return Schedule.builder()
                    .title(title)
                    .startDateTime(startDateTime)
                    .endDateTime(endDateTime)
                    .color(color)
                    .memo(memo)
                    .location(location)
                    .latitude(latitude)
                    .longitude(longitude)
                    .userId(userId)
                    .build();
        }
    }

    @Getter
    public static class ScheduleDetailsResponse {
        private Long id;
        private String title;
        private String color;
        private String startDateTime;
        private String endDateTime;
        private String location;
        private double latitude;
        private double longitude;
        private String memo;
        private final List<ScheduleParticipantResponse> participants = new ArrayList<>();
        private final List<UnregisteredParticipantResponse> unregisteredParticipants = new ArrayList<>();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class DailyScheduleResponse {
        private List<ScheduleListResponse> schedules = new ArrayList<>();
        private List<BirthdayPerson> birthdayPerson;
    }

    @Getter
    @Setter
    public static class ScheduleListResponse {
        private Long id;
        private Long groupId;
        private String title;
        private String location;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        private LocalDateTime startTime;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        private LocalDateTime endTime;
        private String color;
    }

    @Getter
    @Setter
    public static class BirthdayPerson {
        private List<String> names;
    }

    @Getter
    @Setter
    public static class ScheduleCheckResponse {
        private LocalDate date;
        private Boolean isSchedule = false;
        private Boolean isGroupSchedule = false;
        private Boolean isBirthday = false;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class MyScheduleData{
        List<ScheduleCheckResponse> myScheduleData = new ArrayList<>();
    }

}
