package com.planu.group_meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class dataResponse<T> {
    private T data;
}