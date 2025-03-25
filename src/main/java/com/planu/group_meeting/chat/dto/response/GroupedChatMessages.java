package com.planu.group_meeting.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GroupedChatMessages {
    private String chatDate;  // ex) "2025-03-25"
    private List<ChatMessageResponse> messages;
}
