package com.planu.group_meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataResponse<T> {
    private T data;
}