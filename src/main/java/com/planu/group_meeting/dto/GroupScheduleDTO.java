package com.planu.group_meeting.dto;

import com.planu.group_meeting.entity.GroupSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

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

        private Double latitude;

        private Double longitude;

        private String memo;

        private List<String> participants;

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

    @Getter
    public static class ParticipantsResponse {
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
}
