package com.planu.group_meeting.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public class TokenDTO {
    private final String access;
    private final String refresh;
}
