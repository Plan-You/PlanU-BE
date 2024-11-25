package com.planu.group_meeting.dto;

import com.planu.group_meeting.entity.Schedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class ScheduleDto {
    @Data
    public static class ScheduleSaveRequest{

        @NotBlank(message = "일정 제목은 필수 입력 값입니다.")
        private String title;

        @NotNull(message = "시작 날짜와 시간은 필수 입력 값입니다.")
        private LocalDateTime startDateTime;

        @NotNull(message = "종료 날짜와 시간은 필수 입력 값입니다.")
        private LocalDateTime endDateTime;

        private String color;

        private String location;

        private String memo;

        private List<Long>participants;

        public Schedule toEntity(Long userId){
            return Schedule.builder()
                    .title(title)
                    .startDateTime(startDateTime)
                    .endDateTime(endDateTime)
                    .color(color)
                    .memo(memo)
                    .location(location)
                    .userId(userId)
                    .build();
        }

    }

}
