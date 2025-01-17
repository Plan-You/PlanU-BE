package com.planu.group_meeting.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

public class AvailableDateDto {
    @Getter
    public static class AvailableDatesRequest{
        @NotEmpty(message = "가능한 날짜를 입력해주세요.")
        private List<LocalDate> availableDates;
    }

    @Getter
    @AllArgsConstructor
    public static class AvailableDateRatios{
        private List<AvailableDateRatio> availableDateRatios;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class AvailableDateRatio{
        private String date;
        private Double ratio;
    }
}
