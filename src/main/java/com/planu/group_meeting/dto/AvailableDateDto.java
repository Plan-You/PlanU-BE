package com.planu.group_meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

public class AvailableDateDto {
    @Getter
    public static class AvailableDatesRequest{
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

    @Setter
    @Getter
    @AllArgsConstructor
    public static class AvailableDateRank {
        private Long ranks;
        private Integer countOfAvailableMembers;
        private String date;
    }

    @Getter
    @AllArgsConstructor
    public static class AvailableDateRanks {
        private List<AvailableDateRank> availableDateRanks;
    }
}
