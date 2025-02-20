package com.planu.group_meeting.service;

import com.planu.group_meeting.dao.GroupScheduleCommentDAO;
import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.dto.GroupScheduleCommentDTO;
import com.planu.group_meeting.entity.GroupScheduleComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GroupScheduleCommentService {
    private final GroupScheduleCommentDAO groupScheduleCommentDAO;
    private final UserDAO userDAO;

    @Transactional
    public void create(
            Long userId,
            Long groupId,
            Long groupScheduleId,
            GroupScheduleCommentDTO.GroupScheduleCommentRequest groupScheduleComment
    ) {
        GroupScheduleComment comment = groupScheduleComment.toEntity(userId, groupId, groupScheduleId);
        groupScheduleCommentDAO.create(comment);
    }

    @Transactional
    public Map<String, Object> getAllByGroupScheduleId(Long groupId, Long groupScheduleId) {
        Map<String, Object> response = new HashMap<>();
        List<GroupScheduleComment> groupScheduleComments = groupScheduleCommentDAO.getAllByGroupScheduleId(groupId, groupScheduleId);

        LocalDateTime currentTime = LocalDateTime.now();
        response.put("countOfComment", groupScheduleComments.size());

        List<Map<String, Object>> comments = new ArrayList<>();
        for(var comment : groupScheduleComments) {
            Map<String, Object> commentView = new HashMap<>();
            String username = userDAO.findUsernameById(comment.getUserId());
            String name = userDAO.findNameById(comment.getUserId());
            String timestamp = formatTimestamp(currentTime, comment.getCreatedDate());

            commentView.put("username", username);
            commentView.put("name", name);
            commentView.put("timestamp", timestamp);
            commentView.put("message", comment.getMessage());

            comments.add(commentView);
        }

        response.put("comments", comments);
        return response;
    }

    private String formatTimestamp(LocalDateTime currentTime, LocalDateTime createdTime) {
        Duration duration = Duration.between(createdTime, currentTime);
        StringBuilder stringBuilder = new StringBuilder();
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();
        long weeks = days / 7;
        long months = ChronoUnit.MONTHS.between(createdTime, currentTime);
        long years = ChronoUnit.YEARS.between(createdTime, currentTime);

        if(years > 0) {
            stringBuilder.append(years).append("년 전");
        }
        else if(months > 0) {
            stringBuilder.append(months).append("개월 전");
        }
        else if(weeks > 0) {
            stringBuilder.append(weeks).append("주 전");
        }
        else if(days > 0) {
            stringBuilder.append(days).append("일 전");
        }
        else if(hours > 0) {
            stringBuilder.append(hours).append("시간 전");
        }
        else {
            stringBuilder.append(minutes).append("분 전");
        }

        return stringBuilder.toString();
    }
}
