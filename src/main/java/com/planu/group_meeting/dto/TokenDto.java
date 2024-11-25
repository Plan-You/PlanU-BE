package com.planu.group_meeting.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public class TokenDto {
    private final String access;
    private final String refresh;
}
