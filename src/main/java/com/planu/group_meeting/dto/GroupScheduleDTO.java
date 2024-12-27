package com.planu.group_meeting.dto;

import com.planu.group_meeting.entity.GroupSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

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

        private String location;

        private String latitude;

        private String longitude;

        private String memo;

        private List<Long> participants;

        private List<String> unregisteredParticipants;

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
    public static class todayScheduleResponse {
        private Long id;
        private String title;
        private String startDateTime;
        private String location;
    }

    @Getter
    public static class scheduleOverViewResponse {
        private Long id;
        private String title;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
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
        private List<scheduleOverViewResponse> groupSchedules;
    }
}
