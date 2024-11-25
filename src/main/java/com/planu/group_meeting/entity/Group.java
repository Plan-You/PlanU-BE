package com.planu.group_meeting.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group {

    private Long id;
    private String name;
    private String groupImageUrl;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}

