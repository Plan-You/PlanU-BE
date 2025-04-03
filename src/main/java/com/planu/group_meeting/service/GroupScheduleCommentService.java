package com.planu.group_meeting.service;

import static com.planu.group_meeting.dto.GroupScheduleDTO.ParticipantsResponse;
import static com.planu.group_meeting.dto.NotificationDTO.GroupScheduleCommentNotification;

import com.planu.group_meeting.dao.GroupScheduleCommentDAO;
import com.planu.group_meeting.dao.GroupScheduleDAO;
import com.planu.group_meeting.dao.GroupScheduleParticipantDAO;
import com.planu.group_meeting.dao.UserDAO;
import com.planu.group_meeting.dto.GroupScheduleCommentDTO;
import com.planu.group_meeting.entity.GroupSchedule;
import com.planu.group_meeting.entity.GroupScheduleComment;
import com.planu.group_meeting.entity.common.EventType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupScheduleCommentService {
    private final GroupScheduleCommentDAO groupScheduleCommentDAO;
    private final UserDAO userDAO;
    private final GroupScheduleParticipantDAO groupScheduleParticipantDAO;
    private final GroupScheduleDAO groupScheduleDAO;
    private final NotificationService notificationService;

    @Transactional
    public void create(
            Long userId,
            Long groupId,
            Long groupScheduleId,
            GroupScheduleCommentDTO.GroupScheduleCommentRequest groupScheduleComment
    ) {
        GroupScheduleComment comment = groupScheduleComment.toEntity(userId, groupId, groupScheduleId);
        List<Long> participantIds = getParticipantIds(userId, groupId, groupScheduleId);
        GroupSchedule groupSchedule = groupScheduleDAO.findById(groupId, groupScheduleId)
                .orElseThrow();

        sendCommentNotification(userId, participantIds, groupSchedule);
        groupScheduleCommentDAO.create(comment);
    }

    private void sendCommentNotification(Long userId, List<Long> participantIds, GroupSchedule groupSchedule) {
        for (Long id : participantIds) {
            GroupScheduleCommentNotification groupScheduleCommentNotification =
                    new GroupScheduleCommentNotification(userId, id, "[" + groupSchedule.getTitle() + "] " + userDAO.findNameById(userId) + "님이 댓글을 작성했습니다.", groupSchedule.getGroupId(), groupSchedule.getId());
            notificationService.sendNotification(EventType.COMMENT, groupScheduleCommentNotification);
        }
    }

    private List<Long> getParticipantIds(Long userId, Long groupId, Long groupScheduleId) {
        List<ParticipantsResponse> participants = groupScheduleParticipantDAO.findByScheduleId(groupId, groupScheduleId);
        return participants.stream()
                .map(ParticipantsResponse::getUserId)
                .filter(id -> !id.equals(userId))
                .toList();
    }

    @Transactional
    public Map<String, Object> getAllByGroupScheduleId(Long userId, Long groupId, Long groupScheduleId) {
        Map<String, Object> response = new HashMap<>();
        List<GroupScheduleComment> groupScheduleComments = groupScheduleCommentDAO.getAllByGroupScheduleId(groupId, groupScheduleId);

        LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        response.put("countOfComment", groupScheduleComments.size());

        List<Map<String, Object>> comments = new ArrayList<>();
        for (var comment : groupScheduleComments) {
            Map<String, Object> commentView = new HashMap<>();
            String username = userDAO.findUsernameById(comment.getUserId());
            String name = userDAO.findNameById(comment.getUserId());
            String timestamp = formatTimestamp(currentTime, comment.getCreatedDate());

            commentView.put("id", comment.getId());
            commentView.put("username", username);
            commentView.put("name", name);
            commentView.put("timestamp", timestamp);
            commentView.put("message", comment.getMessage());
            commentView.put("isMyComment", comment.getUserId().equals(userId));
            comments.add(commentView);
        }

        response.put("comments", comments);
        return response;
    }

    public void deleteCommentById(Long groupId, Long groupScheduleId, Long commentId) {
        groupScheduleCommentDAO.deleteCommentById(groupId, groupScheduleId, commentId);
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

        if (years > 0) {
            stringBuilder.append(years).append("년 전");
        } else if (months > 0) {
            stringBuilder.append(months).append("개월 전");
        } else if (weeks > 0) {
            stringBuilder.append(weeks).append("주 전");
        } else if (days > 0) {
            stringBuilder.append(days).append("일 전");
        } else if (hours > 0) {
            stringBuilder.append(hours).append("시간 전");
        } else {
            stringBuilder.append(minutes).append("분 전");
        }

        return stringBuilder.toString();
    }
}
