package com.planu.group_meeting.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class GroupRequestDTO {
    private String groupName;
    private MultipartFile groupImage;
}

