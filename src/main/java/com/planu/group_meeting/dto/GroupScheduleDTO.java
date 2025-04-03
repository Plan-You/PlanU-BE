package com.planu.group_meeting.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.planu.group_meeting.entity.GroupSchedule;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class GroupScheduleDTO {
    @Data
    public static class GroupScheduleRequest {

        @NotBlank(message = "일정 제목은 필수 입력 값입니다.")
        @Size(max = 20, message = "일정 제목은 20자 이내로 입력해주세요.")
        @Pattern(regexp = "^[^\\n]*$", message = "일정 제목은 줄넘김 없이 입력해주세요.")
        private String title;

        @NotNull(message = "시작 날짜와 시간은 필수 입력 값입니다.")
        private LocalDateTime startDateTime;

        @NotNull(message = "종료 날짜와 시간은 필수 입력 값입니다.")
        private LocalDateTime endDateTime;

        private String color;

        @NotBlank(message = "그룹 일정 장소는 필수 입력 값입니다.")
        private String location;

        @NotNull(message = "위도 값은 필수 입력 값입니다.")
        @DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90 이하이어야 합니다.")
        private Double latitude;

        @NotNull(message = "경도 값은 필수 입력 값입니다.")
        @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180 이하이어야 합니다.")
        private Double longitude;

        private String memo;

        @NotNull(message = "참석자는 필수 입력 값입니다.")
        @Size(min = 1, message = "최소 한 명 이상의 참석자가 필요합니다.")
        private List<@NotBlank(message = "참석자 이름은 공백일 수 없습니다.") String> participants;

        public GroupSchedule toEntity(Long groupId) {
            return GroupSchedule.builder()
                    .title(title)
                    .startDateTime(startDateTime)
                    .endDateTime(endDateTime)
                    .color(color)
                    .memo(memo)
                    .location(location)
                    .longitude(longitude)
                    .latitude(latitude)
                    .groupId(groupId)
                    .build();
        }
    }

    @Getter
    @Setter
    public static class todayScheduleResponse {
        private Long id;
        private String title;
        private String startDateTime;
        private String location;
    }

    @Getter
    @Setter
    public static class scheduleOverViewResponse {
        private Long id;
        private String title;
        private String location;
        private String startTime;
        private String endTime;
        private String color;
    }

    @Getter
    @AllArgsConstructor
    public static class GroupTodayScheduleResponse {
        private String GroupName;
        private List<todayScheduleResponse> todaySchedules;
    }

    @Getter
    @AllArgsConstructor
    public static class groupOverViewsResponse {
        private List<scheduleOverViewResponse> schedules;
        private List<String> birthdayPerson;
    }

    @Getter
    @ToString
    public static class ParticipantsResponse {
        @JsonIgnore
        private Long userId;

        private String profileImage;
        private String name;
        private String username;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class GroupSchedulesDetailResponse {
        private final String groupScheduleId;
        private final String title;
        private final String color;
        private final String startDate;
        private final String endDate;
        private final String location;
        private final Double latitude;
        private final Double longitude;
        private final String memo;
        private List<ParticipantsResponse> participants;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class GroupCalendarEvent {
        private String date;
        private Boolean isSchedule;
        private Boolean isBirthday;
    }

    @Getter
    @AllArgsConstructor
    public static class ScheduleLocation {
        Double latitude;
        Double longitude;
    }

    @Getter
    @AllArgsConstructor
    public static class GroupScheduleLocation {
        ScheduleLocation groupScheduleLocation;
    }
}
