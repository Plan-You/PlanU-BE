package com.planu.group_meeting.valid;

import com.planu.group_meeting.exception.Group.InvalidInputException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
@Component
public class InputValidator {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB 제한
    private static final String[] ACCEPTED_FILE_TYPES = {"image/jpeg", "image/png", "image/gif"};

    // 그룹명 검증
    public void groupNameValid(String groupName) {
        if (groupName == null || groupName.length() > 15) {
            throw new InvalidInputException("그룹명은 15자 이내로 입력해야 합니다.");
        }
    }

    // 그룹 이미지 검증
    public void groupImageValid(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidInputException("그룹 이미지는 필수 항목입니다.");
        }

        // 파일 크기 제한
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidInputException("그룹 이미지 파일은 5MB를 초과할 수 없습니다.");
        }

        // 파일 형식 검증
        String contentType = file.getContentType();
        if (contentType == null || !Arrays.asList(ACCEPTED_FILE_TYPES).contains(contentType)) {
            throw new InvalidInputException("허용된 이미지 파일 형식은 JPEG, PNG, GIF 입니다.");
        }
    }
}

