package com.planu.group_meeting.chat.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ChatFileRequest {
    private Long groupId;
    private MultipartFile file;
}
