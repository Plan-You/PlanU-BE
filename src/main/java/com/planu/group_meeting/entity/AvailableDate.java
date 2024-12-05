package com.planu.group_meeting.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AvailableDate {
    private Long id;
    private Long userId;
    private LocalDate possibleDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AvailableDate(Long userId, LocalDate possibleDate){
        this.userId = userId;
        this.possibleDate = possibleDate;
    }
}
