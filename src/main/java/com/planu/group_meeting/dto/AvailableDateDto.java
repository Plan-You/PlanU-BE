package com.planu.group_meeting.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class AvailableDateDto {
    @Getter
    public static class AvailableDatesRequest{
        @NotEmpty(message = "가능한 날짜를 입력해주세요.")
        private List<LocalDate> availableDates;
    }



}
